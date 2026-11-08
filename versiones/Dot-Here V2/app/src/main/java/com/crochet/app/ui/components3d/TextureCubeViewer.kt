package com.crochet.app.ui.components3d

import android.content.Context
import android.graphics.*
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.crochet.app.domain.model.ProductFace
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Visor 3D interactivo en Jetpack Compose que renderiza un cubo/paralelepípedo
 * con las 6 texturas capturadas (Frontal, Trasera, Izquierda, Derecha, Superior, Inferior).
 *
 * Optimizaciones:
 * - Caché de rutas para evitar decodificar imágenes 60 FPS en recomposición.
 * - Ciclo de vida controlado (onResume/onPause) para prevenir caídas de EGL.
 * - Validación y manejo de errores silencioso en shaders y subida de texturas.
 * - Texturas a resolución optimizada (512px) con compresión eficiente de memoria.
 */
@Composable
fun TextureCubeViewer(
    modifier: Modifier = Modifier,
    frontPath: String? = null,
    backPath: String? = null,
    leftPath: String? = null,
    rightPath: String? = null,
    topPath: String? = null,
    bottomPath: String? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isAutoRotating by remember { mutableStateOf(true) }
    var glViewRef by remember { mutableStateOf<CubeGLSurfaceView?>(null) }

    // Control del ciclo de vida para evitar cuelgues o cierres al pausar/reanudar
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> glViewRef?.onResume()
                Lifecycle.Event.ON_PAUSE -> glViewRef?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            glViewRef?.destroy()
            glViewRef?.onPause()
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1A18))
    ) {
        AndroidView(
            factory = { context ->
                CubeGLSurfaceView(context).apply {
                    updateTextures(
                        front = frontPath,
                        back = backPath,
                        left = leftPath,
                        right = rightPath,
                        top = topPath,
                        bottom = bottomPath
                    )
                    glViewRef = this
                }
            },
            update = { view ->
                view.updateTextures(
                    front = frontPath,
                    back = backPath,
                    left = leftPath,
                    right = rightPath,
                    top = topPath,
                    bottom = bottomPath
                )
                view.setAutoRotate(isAutoRotating)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Controles superpuestos en la esquina superior derecha
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Alternar rotación automática
            FilledTonalIconButton(
                onClick = {
                    isAutoRotating = !isAutoRotating
                    glViewRef?.setAutoRotate(isAutoRotating)
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isAutoRotating) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isAutoRotating) "Pausar giro" else "Iniciar giro"
                )
            }

            // Reiniciar cámara a vista frontal
            FilledTonalIconButton(
                onClick = { glViewRef?.resetOrientation() },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reiniciar vista"
                )
            }
        }

        // Barra inferior con accesos rápidos para fijar caras
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FaceSnapButton(label = "Front") { glViewRef?.snapToFace(ProductFace.FRONT) }
                FaceSnapButton(label = "Atrás") { glViewRef?.snapToFace(ProductFace.BACK) }
                FaceSnapButton(label = "Izq") { glViewRef?.snapToFace(ProductFace.LEFT) }
                FaceSnapButton(label = "Der") { glViewRef?.snapToFace(ProductFace.RIGHT) }
                FaceSnapButton(label = "Arriba") { glViewRef?.snapToFace(ProductFace.TOP) }
                FaceSnapButton(label = "Abajo") { glViewRef?.snapToFace(ProductFace.BOTTOM) }
            }
        }
    }
}

@Composable
private fun FaceSnapButton(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

/**
 * GLSurfaceView personalizado que maneja gestos de arrastre (rotación) y pinza (zoom).
 */
class CubeGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: CubeRenderer
    private var previousX = 0f
    private var previousY = 0f
    private val scaleDetector: ScaleGestureDetector

    // Caché de rutas para evitar recargas y decodificaciones innecesarias en cada recomposición
    private var lastFront: String? = null
    private var lastBack: String? = null
    private var lastLeft: String? = null
    private var lastRight: String? = null
    private var lastTop: String? = null
    private var lastBottom: String? = null

    init {
        setEGLContextClientVersion(2)
        renderer = CubeRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                renderer.zoom(detector.scaleFactor)
                return true
            }
        })
    }

    fun updateTextures(
        front: String?,
        back: String?,
        left: String?,
        right: String?,
        top: String?,
        bottom: String?
    ) {
        // Optimización clave: Si las rutas no han cambiado, NO recargar de disco ni reenviar a OpenGL
        if (front == lastFront && back == lastBack && left == lastLeft &&
            right == lastRight && top == lastTop && bottom == lastBottom
        ) {
            return
        }

        lastFront = front
        lastBack = back
        lastLeft = left
        lastRight = right
        lastTop = top
        lastBottom = bottom

        queueEvent {
            renderer.loadTextures(front, back, left, right, top, bottom)
        }
        requestRender()
    }

    fun setAutoRotate(enabled: Boolean) {
        renderer.isAutoRotating = enabled
    }

    fun destroy() {
        queueEvent {
            renderer.releaseGL()
        }
    }

    fun resetOrientation() {
        renderer.resetView()
    }

    fun snapToFace(face: ProductFace) {
        renderer.snapTo(face)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        if (event.pointerCount > 1) {
            return true
        }

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = x
                previousY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                renderer.rotate(dx * 0.4f, dy * 0.4f)
                previousX = x
                previousY = y
            }
        }
        return true
    }
}

/**
 * Renderizador OpenGL ES 2.0 para el cubo texturizado.
 */
class CubeRenderer(private val context: Context) : GLSurfaceView.Renderer {

    @Volatile var isAutoRotating = true

    private var angleX = 20f
    private var angleY = -35f
    private var scale = 1.0f

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val scratchMatrix = FloatArray(16)

    private var program = 0
    private var positionHandle = 0
    private var texCoordHandle = 0
    private var mvpMatrixHandle = 0
    private var textureHandle = 0

    private val textureIds = IntArray(6) // 0: FRONT, 1: BACK, 2: LEFT, 3: RIGHT, 4: TOP, 5: BOTTOM
    private val facePaths = arrayOfNulls<String>(6)
    private var texturesNeedUpdate = false

    private val vertexBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer

    init {
        // Coordenadas 3D para cada cara (4 vértices por cara x 6 caras = 24 vértices)
        val vertices = floatArrayOf(
            // FRONT (+Z)
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,
             1.0f,  1.0f,  1.0f,

            // BACK (-Z)
             1.0f,  1.0f, -1.0f,
             1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            // LEFT (-X)
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,

            // RIGHT (+X)
             1.0f,  1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,
             1.0f, -1.0f, -1.0f,
             1.0f,  1.0f, -1.0f,

            // TOP (+Y)
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
             1.0f,  1.0f,  1.0f,
             1.0f,  1.0f, -1.0f,

            // BOTTOM (-Y)
            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
             1.0f, -1.0f, -1.0f,
             1.0f, -1.0f,  1.0f
        )

        val texCoords = floatArrayOf(
            // Mapeo UV estándar por cada cara (0,0 en esquina sup izq)
            0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f,
            0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f,
            0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f,
            0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f,
            0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f,
            0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f
        )

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(texCoords)
                position(0)
            }
        }
    }

    fun loadTextures(
        front: String?,
        back: String?,
        left: String?,
        right: String?,
        top: String?,
        bottom: String?
    ) {
        facePaths[0] = front
        facePaths[1] = back
        facePaths[2] = left
        facePaths[3] = right
        facePaths[4] = top
        facePaths[5] = bottom
        texturesNeedUpdate = true
    }

    fun rotate(dx: Float, dy: Float) {
        angleY += dx
        angleX += dy
        angleX = angleX.coerceIn(-89f, 89f)
    }

    fun zoom(factor: Float) {
        scale = (scale * factor).coerceIn(0.4f, 2.8f)
    }

    fun resetView() {
        angleX = 20f
        angleY = -35f
        scale = 1.0f
    }

    fun snapTo(face: ProductFace) {
        isAutoRotating = false
        when (face) {
            ProductFace.FRONT -> { angleX = 0f; angleY = 0f }
            ProductFace.BACK -> { angleX = 0f; angleY = 180f }
            ProductFace.LEFT -> { angleX = 0f; angleY = 90f }
            ProductFace.RIGHT -> { angleX = 0f; angleY = -90f }
            ProductFace.TOP -> { angleX = 89f; angleY = 0f }
            ProductFace.BOTTOM -> { angleX = -89f; angleY = 0f }
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.12f, 0.10f, 0.09f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        // Shaders GLSL
        val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 aPosition;
            attribute vec2 aTexCoordinate;
            varying vec2 vTexCoordinate;
            void main() {
                gl_Position = uMVPMatrix * aPosition;
                vTexCoordinate = aTexCoordinate;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            uniform sampler2D uTexture;
            varying vec2 vTexCoordinate;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoordinate);
            }
        """.trimIndent()

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoordinate")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture")

        GLES20.glGenTextures(6, textureIds, 0)
        texturesNeedUpdate = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        try {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            if (program == 0 || positionHandle < 0 || texCoordHandle < 0) return

            if (texturesNeedUpdate) {
                uploadTextures()
                texturesNeedUpdate = false
            }

            if (isAutoRotating) {
                angleY += 0.5f
                if (angleY > 360f) angleY -= 360f
            }

            // Posición de cámara
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4.5f / scale, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f)
            Matrix.rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f)

            Matrix.multiplyMM(scratchMatrix, 0, vPMatrix, 0, modelMatrix, 0)

            GLES20.glUseProgram(program)

            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

            GLES20.glEnableVertexAttribArray(texCoordHandle)
            GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratchMatrix, 0)

            // Dibujar cada una de las 6 caras con su respectiva textura
            for (face in 0..5) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[face])
                GLES20.glUniform1i(textureHandle, 0)

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, face * 4, 4)
            }

            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(texCoordHandle)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun uploadTextures() {
        try {
            val faces = ProductFace.entries
            for (i in 0..5) {
                val path = facePaths[i]
                val bitmap = loadOrGenerateBitmap(path, faces[i])

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[i])
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun loadOrGenerateBitmap(path: String?, face: ProductFace): Bitmap {
        if (!path.isNullOrBlank()) {
            try {
                val file = File(path)
                if (file.exists() && file.length() > 0) {
                    val boundsOpts = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(file.absolutePath, boundsOpts)

                    val maxDim = maxOf(boundsOpts.outWidth, boundsOpts.outHeight)
                    var sampleSize = 1
                    while (maxDim / (sampleSize * 2) >= 512) {
                        sampleSize *= 2
                    }

                    val opts = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = Bitmap.Config.RGB_565 // Ahorro del 50% de memoria VRAM
                    }
                    val bmp = BitmapFactory.decodeFile(file.absolutePath, opts)
                    if (bmp != null) return bmp
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Si la foto aún no ha sido capturada, generamos una textura de marcador decorativa
        val bmp = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val bgPaint = Paint().apply {
            color = android.graphics.Color.rgb(200, 90, 50) // Terracota Dot-Here
        }
        val borderPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        val subPaint = Paint().apply {
            color = android.graphics.Color.rgb(255, 220, 207)
            textSize = 18f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        canvas.drawRect(0f, 0f, 256f, 256f, bgPaint)
        canvas.drawRect(4f, 4f, 252f, 252f, borderPaint)
        canvas.drawText(face.title, 128f, 120f, textPaint)
        canvas.drawText("Sin textura", 128f, 155f, subPaint)

        return bmp
    }

    fun releaseGL() {
        try {
            if (textureIds[0] != 0) {
                GLES20.glDeleteTextures(6, textureIds, 0)
                for (i in 0..5) {
                    textureIds[i] = 0
                }
            }
            if (program != 0) {
                GLES20.glDeleteProgram(program)
                program = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}

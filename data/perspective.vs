attribute vec3 aPosition;
attribute vec3 aColor;

varying vec3 vColor;

uniform mat4 uProjection;
uniform mat4 uModelView;

void main()
{
    vColor = aColor;

    gl_Position = uProjection * uModelView * vec4(aPosition, 1.0);
}


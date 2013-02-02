#version 120

attribute vec3 aPosition;
attribute vec3 aColor;
attribute vec3 aNormal;

varying vec3 vPosition;
varying vec3 vColor;
varying vec3 vNormal;

uniform mat4 uProjection;
uniform mat4 uModelView;

void main()
{
    vColor = aColor;
    vNormal = normalize(mat3(uModelView) * aNormal);
    vPosition = (uModelView * vec4(aPosition, 1.0)).xyz;
    gl_Position = uProjection * vec4(vPosition, 1.0);
}


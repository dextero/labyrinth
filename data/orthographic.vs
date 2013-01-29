attribute vec3 aPosition;
attribute vec2 aTexcoord;

varying vec2 vTexcoord;

uniform mat4 uProjection;
uniform mat4 uModelView;

void main()
{
    vTexcoord = aTexcoord;

    gl_Position = uProjection * uModelView * vec4(aPosition, 1.0);
}


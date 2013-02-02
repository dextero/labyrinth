varying vec2 vTexcoord;

uniform sampler2D uTexture0;

void main()
{
    gl_FragColor = texture2D(uTexture0, vTexcoord);
}

varying vec2 vTexcoord;

uniform sampler2D uTexture0;

void main()
{
    vec4 col = texture2D(uTexture0, vTexcoord);
    gl_FragColor = col + vec4(0.5, 0.5, 0.5, 1.0);
}

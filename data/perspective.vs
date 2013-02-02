attribute vec3 aPosition;
attribute vec3 aColor;

varying vec3 vColor;

uniform mat4 uProjection;
uniform mat4 uModelView;

void main()
{
    vColor = aColor;

#if 0
    mat4 projMat =
#  if 1
        /* perspective */
        mat4(1., 0., 0., 0.,
           0., 1., 0., 0.,
           0., 0., -1.01005, -1.00503,
           0., 0., -1., 0.);
#  else
        /* ortho */
        mat4(1., 0., 0., 0.,
           0., 1., 0., 0.,
           0., 0., -1., 0.,
           0., 0., 0., 1.);
#  endif

    mat4 mvMat =
        mat4(1., 0., 0., 0.,
             0., 1., 0., 0.,
             0., 0., 1., -10.,
             0., 0., 0., 1.);

    gl_Position = projMat * mvMat * vec4(aPosition, 1.0);
#else
    gl_Position = uProjection * uModelView * vec4(aPosition, 1.0);
#endif
}


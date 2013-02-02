varying vec3 vPosition;
varying vec3 vColor;
varying vec3 vNormal;

void main()
{
    vec3 ambient = vec3(0.1);

    vec3 lightPos = vec3(0.0, 50.0, 50.0);
    vec3 lightDir = normalize(vPosition - lightPos);
    vec3 diffuse = vColor * clamp(dot(vNormal, -lightDir), 0.0, 1.0);

    vec3 viewDir = normalize(-vPosition);
    vec3 half = normalize(lightDir + viewDir);
    float intensity = pow(clamp(dot(vNormal, half), 0.0, 1.0), 16.0);
    vec3 specular = vec3(1.0) * intensity;

    vec3 color = clamp(ambient
                       + diffuse
                       //+ specular
                       , 0.0, 1.0);
    gl_FragColor = vec4(color, 1.0);
}

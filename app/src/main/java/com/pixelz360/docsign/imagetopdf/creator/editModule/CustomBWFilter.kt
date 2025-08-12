package com.pixelz360.docsign.imagetopdf.creator.editModule

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class CustomBWFilter : GPUImageFilter(NO_FILTER_VERTEX_SHADER, """
    precision mediump float;
    varying vec2 textureCoordinate;
    uniform sampler2D inputImageTexture;

    void main() {
        vec4 color = texture2D(inputImageTexture, textureCoordinate);
        float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
        float threshold = 0.5;
        float bw = step(threshold, gray);
        gl_FragColor = vec4(vec3(bw), color.a);
    }
""")

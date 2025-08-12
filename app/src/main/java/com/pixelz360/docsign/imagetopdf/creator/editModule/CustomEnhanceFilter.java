package com.pixelz360.docsign.imagetopdf.creator.editModule;

// CustomEnhanceFilter.java
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils;

// CustomEnhanceFilter.java
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

// CustomEnhanceFilter.java
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class CustomEnhanceFilter extends GPUImageFilter {
    private static final String ENHANCE_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "void main() {\n" +
            "    lowp vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    gl_FragColor = vec4(color.r * 1.2, color.g * 1.2, color.b * 1.2, color.a);\n" +
            "}";

    public CustomEnhanceFilter() {
        super(NO_FILTER_VERTEX_SHADER, ENHANCE_FRAGMENT_SHADER);
    }
}


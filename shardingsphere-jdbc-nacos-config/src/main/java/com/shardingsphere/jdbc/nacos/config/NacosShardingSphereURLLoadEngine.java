//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shardingsphere.jdbc.nacos.config;

import java.util.Arrays;
import java.util.Collection;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.infra.url.core.arg.URLArgumentLineRender;
import org.apache.shardingsphere.infra.url.core.arg.URLArgumentPlaceholderTypeFactory;
import org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader;

public final class NacosShardingSphereURLLoadEngine {
    private final NacosShardingSphereURL url;
    private final ShardingSphereURLLoader urlLoader;

    public NacosShardingSphereURLLoadEngine(NacosShardingSphereURL url) {
        this.url = url;
        this.urlLoader = (ShardingSphereURLLoader)TypedSPILoader.getService(ShardingSphereURLLoader.class, url.getSourceType());
    }

    public byte[] loadContent() {
        Collection<String> lines = Arrays.asList(this.urlLoader.load(this.url.getConfigurationSubject(), this.url.getQueryProps()).split(System.lineSeparator()));
        return URLArgumentLineRender.render(lines, URLArgumentPlaceholderTypeFactory.valueOf(this.url.getQueryProps()));
    }
}

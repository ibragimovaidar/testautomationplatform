package ru.itis.ibragimovaidar.jooqgenerator.jooq.listener;

import io.qameta.allure.Allure;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.TXTFormat;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.StringUtils;

import java.nio.charset.StandardCharsets;

public class AllureExecuteListener extends DefaultExecuteListener {

    @Override
    public void resultEnd(ExecuteContext ctx) {
        Configuration configuration = ctx.configuration();

        var sql = "";
        var result = "";
        var fetchedRows = 0;

        if (ctx.query() != null) {
            sql = DSL.using(configuration.dialect(), new Settings().withRenderFormatted(true)).renderInlined(ctx.query());
        } else if (ctx.routine() != null) {
            sql = DSL.using(configuration.dialect(), new Settings().withRenderFormatted(true)).renderInlined(ctx.routine());
        } else if (!StringUtils.isBlank(ctx.sql())) {
            sql = ctx.sql();
        }

        var cxtResult = ctx.result();
        if (cxtResult != null) {
            fetchedRows = cxtResult.size();
            result = cxtResult.format(TXTFormat.DEFAULT.maxRows(5).maxColWidth(50));
        }

        if (!StringUtils.isBlank(sql)) {
            var lifecycle = Allure.getLifecycle();
            if (lifecycle.getCurrentTestCase().isPresent()) {
                lifecycle.addAttachment(
                        "SQL",
                        "text/html",
                        "html",
                        //language=HTML
                        """
                                <div><pre class="preformated-text">%s</pre></div>
                                        
                                <h4>Fetched %d row(s):</h4>
                                <div>
                                   <div><pre class="preformated-text">%s</pre></div>
                                </div>
                                """.formatted(sql, fetchedRows, result).getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}

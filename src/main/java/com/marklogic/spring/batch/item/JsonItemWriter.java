package com.marklogic.spring.batch.item;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by sanjuthomas on 5/24/16.
 */
public class JsonItemWriter implements ItemWriter<ObjectNode> {

    private UriGenerator uriGenerator = new DefaultUriGenerator();

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    @Override
    public void write(List<? extends ObjectNode> items) throws Exception {
        DatabaseClient client = databaseClientProvider.getDatabaseClient();
        JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
        items.forEach(item -> {
            jsonDocumentManager.write(uriGenerator.generate(), new JacksonHandle(item));
        });
    }
}

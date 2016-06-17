package com.marklogic.example;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.example.geonames.GeonameFieldSetMapper;
import com.marklogic.example.geonames.GeonamesItemProcessor;
import org.geonames.Geoname;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

import java.util.List;

@Configuration
public class IngestGeonamesToMarkLogicConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("ingestGeonames").start(step1).build();
    }

    @Bean
    protected Step step1(ItemReader<Geoname> reader, ItemProcessor<Geoname, Document> processor, ItemWriter<Document> writer) {
        return stepBuilderFactory.get("step1")
                .<Geoname, Document> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                //.taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    protected ItemReader<Geoname> geonameReader() {
        FlatFileItemReader<Geoname> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("cities15000.txt"));
        DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        tokenizer.setQuoteCharacter('{');
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new GeonameFieldSetMapper());
        reader.setLineMapper(mapper);
        return reader;
    }

    @Bean
    protected ItemProcessor<Geoname, Document> processor() {
        return new GeonamesItemProcessor();
    }

    @Bean
    protected ItemWriter<Document> writer() {
        return new ItemWriter<Document>() {
            @Override
            public void write(List<? extends Document> items) throws Exception {

            }
        };
    }
}
package com.levi.wholesale.config;

import com.levi.wholesale.batch.listener.BatchListener;
import com.levi.wholesale.batch.tasklet.PacProcessingTasklet;
import com.levi.wholesale.batch.tasklet.PricingProcessingTasklet;
import com.levi.wholesale.batch.tasklet.VirProcessingTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class BatchConfig extends DefaultBatchConfigurer {

    @Autowired
    private BatchListener batchListener;

    @Autowired
    private VirProcessingTasklet virProcessingTasklet;

    @Autowired
    private PacProcessingTasklet pacProcessingTasklet;

    @Autowired
    private PricingProcessingTasklet pricingProcessingTasklet;

    @Autowired
    @Qualifier("jpaTransactionManager")
    private JpaTransactionManager jpaTransactionManager;

    @Override
    public void setDataSource(DataSource dataSource) {
        // initialize will use a Map based JobRepository (instead of database)
    }

    @Bean("cxFeedProcessingJob")
    public Job processPacVirJob(JobBuilderFactory jobBuilders,
                                StepBuilderFactory stepBuilders) {
        return jobBuilders.get("cxFeedProcessingJob")
                .start(processVirStep(stepBuilders))
                .next(processPacStep(stepBuilders))
                .next(processPricingStep(stepBuilders))
                .listener(batchListener)
                .build();
    }

    @Bean
    public Step processVirStep(StepBuilderFactory stepBuilders) {
        return stepBuilders.get("processVirStep")
                .transactionManager(jpaTransactionManager)
                .tasklet(virProcessingTasklet).build();
    }

    @Bean
    public Step processPacStep(StepBuilderFactory stepBuilders) {
        return stepBuilders.get("processPacStep")
                .transactionManager(jpaTransactionManager)
                .tasklet(pacProcessingTasklet).build();
    }

    @Bean
    public Step processPricingStep(StepBuilderFactory stepBuilders) {
        return stepBuilders.get("processPricingStep")
                .transactionManager(jpaTransactionManager)
                .tasklet(pricingProcessingTasklet).build();
    }
}

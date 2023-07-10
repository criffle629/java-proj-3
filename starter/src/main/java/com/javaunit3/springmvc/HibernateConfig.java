package com.javaunit3.springmvc;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.*;
 
import com.javaunit3.springmvc.model.MovieEntity;
import com.javaunit3.springmvc.model.VoteEntity;
 
@Configuration
public class HibernateConfig {

    @Bean
    public SessionFactory getSessionFactory() {
        return new org.hibernate.cfg.Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(MovieEntity.class)
            .addAnnotatedClass(VoteEntity.class)
            .buildSessionFactory();
    }


}
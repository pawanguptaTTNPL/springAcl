package com.example.springacl.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
        import org.springframework.jdbc.datasource.DriverManagerDataSource;
        import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
        import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.acls.AclPermissionEvaluator;
        import org.springframework.security.acls.domain.*;
        import org.springframework.security.acls.jdbc.BasicLookupStrategy;
        import org.springframework.security.acls.jdbc.JdbcMutableAclService;
        import org.springframework.security.acls.jdbc.LookupStrategy;
        import org.springframework.security.acls.model.AclService;
        import org.springframework.security.acls.model.PermissionGrantingStrategy;
        import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
        import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
        import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ProjectConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler;


    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return defaultMethodSecurityExpressionHandler;
    }

    @Bean
    public DataSource dataSource() {
        var ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/springacl");
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }

    @Bean
    public AclService aclService() {
        return new JdbcMutableAclService(dataSource(), lookupStrategy(), aclCache());
    }

    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    @Bean
    public EhCacheBasedAclCache aclCache() {
        return new EhCacheBasedAclCache(
                aclEhCacheFactoryBean().getObject(),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(() -> "read");
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(
                dataSource(),
                aclCache(),
                aclAuthorizationStrategy(),
                new ConsoleAuditLogger()
        );
    }

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        var permissionEvaluator  = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }
    @Bean
    public RoleHierarchyImpl roleHierarchy() {

        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("write > read");
        return roleHierarchy;
    }
    @Bean
    public RoleHierarchyVoter roleVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }
}
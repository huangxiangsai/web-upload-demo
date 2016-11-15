package com.devsai.view;

/**
 * Created by huangxiangsai on 16/6/21.
 */

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

public class MultiUrlViewResolver extends AbstractCachingViewResolver {

    private Map<String, ViewResolver> resolvers;

    private ThreadLocal<Boolean> loadview=new ThreadLocal<Boolean>();

    public boolean isLoading(){
        try{
            return this.loadview.get();
        }catch(Exception e){
            return false;
        }
    }

    @PostConstruct
    public void init(){
        this.loadview.set(false);
    }

    public Map<String, ViewResolver> getResolvers() {
        return resolvers;
    }

    public void setResolvers(Map<String, ViewResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {

        ViewResolverResult res=findViewResolver(viewName);

        this.loadview.set(true);
        View view= res.getResolver().resolveViewName(res.getViewName(), locale);
        this.loadview.set(false);
        return view;
    }

    private  Pattern viewType=Pattern.compile("^(\\w+):(.*)");

    private ViewResolverResult findViewResolver(String viewName){
        Matcher m=viewType.matcher(viewName);
        if(m.matches()){
            String curName=m.group(2);
            ViewResolver vr=this.resolvers.get(m.group(1));
            if(vr==null){
                vr=this.resolvers.get("jsp");
            }
            return new ViewResolverResult(curName, vr);
        }
        return new ViewResolverResult(viewName, resolvers.get("jsp"));
    }

    private static final class ViewResolverResult{
        private String viewName;

        private ViewResolver resolver;

        public ViewResolverResult(String viewName,ViewResolver resolver) {
            this.viewName=viewName;
            this.resolver=resolver;
        }

        public String getViewName() {
            return viewName;
        }
        public ViewResolver getResolver() {
            return resolver;
        }

        @Override
        public String toString() {
            return this.viewName;
        }
    }
}

package sumdu.edu.ua.web.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommentServiceLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceLoggingAspect.class);

    @Around("execution(* sumdu.edu.ua.core.service.CommentService.delete(..))")
    public Object logDeleteCall(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object[] args = pjp.getArgs();

        log.info("Calling CommentService.delete(bookId={}, commentId={})", args[0], args[1]);

        try {
            Object result = pjp.proceed();
            log.info("Finished in {} ms", System.currentTimeMillis() - start);
            return result;
        } catch (Exception ex) {
            log.warn("Failed in {} ms: {}", System.currentTimeMillis() - start, ex.getMessage());
            throw ex;
        }
    }
}
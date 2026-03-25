package com.example.order.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
  import com.fasterxml.jackson.databind.SerializationFeature;
  import lombok.extern.slf4j.Slf4j;
  import org.aspectj.lang.ProceedingJoinPoint;
  import org.aspectj.lang.annotation.Around;
  import org.aspectj.lang.annotation.Aspect;
  import org.springframework.stereotype.Component;

  import java.util.Arrays;
  import java.util.List;
  import java.util.Map;

  @Slf4j
  @Aspect
  @Component
  public class LoggingAspect {

      private static final List<String> SENSITIVE_FIELDS = Arrays.asList(
              "password", "secret", "token", "apiKey", "creditCard"
      );

      private final ObjectMapper objectMapper;

      public LoggingAspect() {
          this.objectMapper = new ObjectMapper();
          this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      }

      @Around("execution(* com.example.order.service..*.*(..)) || " +
              "execution(* com.example.order.controller..*.*(..))")
      public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
          String className = joinPoint.getTarget().getClass().getSimpleName();
          String methodName = joinPoint.getSignature().getName();

          // Log inputs (with sensitive data masking)
          log.info("→ {}.{} - Input: {}", className, methodName,
                   maskSensitiveData(joinPoint.getArgs()));

          long startTime = System.currentTimeMillis();
          Object result;

          try {
              result = joinPoint.proceed();
              long executionTime = System.currentTimeMillis() - startTime;

              // Log outputs (with sensitive data masking)
              log.info("← {}.{} - Output: {} ({}ms)", className, methodName,
                       maskSensitiveData(result), executionTime);

              return result;
          } catch (Exception e) {
              long executionTime = System.currentTimeMillis() - startTime;
              log.error("✗ {}.{} - Error: {} ({}ms)", className, methodName,
                       e.getMessage(), executionTime);
              throw e;
          }
      }

      private String maskSensitiveData(Object data) {
          if (data == null) {
              return "null";
          }

          try {
              String json = objectMapper.writeValueAsString(data);
              return maskSensitiveFields(json);
          } catch (Exception e) {
              return data.toString();
          }
      }

      private String maskSensitiveFields(String json) {
          for (String field : SENSITIVE_FIELDS) {
              json = json.replaceAll(
                  "\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                  "\"" + field + "\":\"*****\""
              );
          }
          return json;
      }
  }
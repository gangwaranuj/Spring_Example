package com.workmarket.velvetrope;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class VelvetRopeAspect {
	@Autowired private AuthenticatedGuestService authenticatedGuestService;
	@Autowired private UnauthenticatedGuestService unauthenticatedGuestService;

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {}

	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void controllerBean() {}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void requestMapping() {}

	@Pointcut(value = "args(guest, *)", argNames = "guest")
	public void leadingGuestArg(Guest guest) {}

	@Pointcut(value = "controllerBean() && @annotation(velvetRope) && publicMethod() && requestMapping()", argNames = "velvetRope")
	public void annotatedMethod(VelvetRope velvetRope) {}

	@Pointcut(value = "controllerBean() && @within(velvetRope) && publicMethod() && requestMapping()", argNames = "velvetRope")
	public void annotatedClass(VelvetRope velvetRope) {}

	@Pointcut(value = "@within(velvetRope) && publicMethod() && leadingGuestArg(guest)", argNames = "velvetRope,guest")
	public void annotatedGuestMethod(VelvetRope velvetRope, Guest guest) {}

	@Around(value = "annotatedMethod(velvetRope)", argNames = "pjp,velvetRope")
	public Object checkAnnotatedMethod(ProceedingJoinPoint pjp, VelvetRope velvetRope) throws Throwable {
		return proceedOrThrow(pjp, velvetRope);
	}

	@Around(value = "annotatedClass(velvetRope)", argNames = "pjp,velvetRope")
	public Object checkAnnotatedClass(ProceedingJoinPoint pjp, VelvetRope velvetRope) throws Throwable {
		return proceedOrThrow(pjp, velvetRope);
	}

	@Around(value = "annotatedGuestMethod(velvetRope, guest)", argNames = "pjp,velvetRope,guest")
	public Object checkAnnotatedGuestMethod(ProceedingJoinPoint pjp, VelvetRope velvetRope, Guest guest) throws Throwable {
		return proceedOrNot(pjp, velvetRope, guest);
	}

	private Object proceedOrNot(ProceedingJoinPoint pjp, VelvetRope velvetRope, Guest guest) throws Throwable {
		if (shouldEnter(guest, velvetRope)) {
			return pjp.proceed();
		}
		return null;
	}

	private Object proceedOrThrow(ProceedingJoinPoint pjp, VelvetRope velvetRope) throws Throwable {
		if (shouldEnter(velvetRope)) {
			return pjp.proceed();
		} else {
			throw new UnauthorizedVenueException(velvetRope);
		}
	}

	private boolean shouldEnter(VelvetRope velvetRope) {
		return velvetRope.bypass() ^ getGuest().canEnter(velvetRope.venue());
	}

	private boolean shouldEnter(Guest guest, VelvetRope velvetRope) {
		return velvetRope.bypass() ^ getGuest(guest).canEnter(velvetRope.venue());
	}

	private Guest getGuest() {
		return authenticatedGuestService.getGuest();
	}

	private Guest getGuest(Guest guest) {
		return unauthenticatedGuestService.getGuest(guest);
	}
}

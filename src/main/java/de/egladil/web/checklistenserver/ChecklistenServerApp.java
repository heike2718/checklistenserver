//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

/**
 * ChecklistenServerApp
 */
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/checklisten-api")
public class ChecklistenServerApp extends Application {

//	@Override
//	public Set<Class<?>> getClasses() {
//
//		Set<Class<?>> classes = new HashSet<>();
//
//		// microprofile jwt auth filters
////		classes.add(JWTAuthorizationFilter.class); beim signup
//		// Verwenden eigene Implementierung, da es ein paar Besonderheiten gibt
//		classes.add(AuthorizationFilter.class);
//		classes.add(JWTRolesAllowedDynamicFeature.class);
//
//        classes.add(ContentSecurityPolicyFilter.class);
//        classes.add(SecureHeadersFilter.class);
//        classes.add(MDCFilter.class);
//
//		classes.add(ChecklistenExceptionMapper.class);
//
//		// resources
//		classes.add(ChecklistenResource.class);
//
//		classes.add(ChecklistenTemplateResource.class);
//
//		classes.add(SessionController.class);
//
//		classes.add(SignUpResource.class);
//
//		classes.add(UserResource.class);
//
//		classes.add(DevelopmentResource.class);
//
//		return classes;
//	}

}

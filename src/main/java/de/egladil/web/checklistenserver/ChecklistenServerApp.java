//=====================================================
// Projekt: checklistenserver
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.checklistenserver;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

import com.kumuluz.ee.jwt.auth.feature.JWTRolesAllowedDynamicFeature;

import de.egladil.web.checklistenserver.endpoints.ChecklistenController;
import de.egladil.web.checklistenserver.endpoints.ChecklistenTemplateController;
import de.egladil.web.checklistenserver.endpoints.DevelopmentController;
import de.egladil.web.checklistenserver.endpoints.SessionController;
import de.egladil.web.checklistenserver.endpoints.SignUpController;
import de.egladil.web.checklistenserver.endpoints.UserController;
import de.egladil.web.checklistenserver.error.ChecklistenExceptionMapper;
import de.egladil.web.checklistenserver.filters.AuthorizationFilter;
import de.egladil.web.checklistenserver.filters.ContentSecurityPolicyFilter;
import de.egladil.web.checklistenserver.filters.MDCFilter;
import de.egladil.web.checklistenserver.filters.SecureHeadersFilter;

/**
 * ChecklistenServerApp
 */
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/checklisten-api")
public class ChecklistenServerApp extends Application {

	@Override
	public Set<Class<?>> getClasses() {

		Set<Class<?>> classes = new HashSet<>();

		// microprofile jwt auth filters
//		classes.add(JWTAuthorizationFilter.class); beim signup
		// Verwenden eigene Implementierung, da es ein paar Besonderheiten gibt
		classes.add(AuthorizationFilter.class);
		classes.add(JWTRolesAllowedDynamicFeature.class);

        classes.add(ContentSecurityPolicyFilter.class);
        classes.add(SecureHeadersFilter.class);
        classes.add(MDCFilter.class);

		classes.add(ChecklistenExceptionMapper.class);

		// resources
		classes.add(ChecklistenController.class);

		classes.add(ChecklistenTemplateController.class);

		classes.add(SessionController.class);

		classes.add(SignUpController.class);

		classes.add(UserController.class);

		classes.add(DevelopmentController.class);

		return classes;
	}

}

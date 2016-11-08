package ge.msda.commons.verssionmanagement.service;

import ge.msda.commons.rest.request.ActionPerformer;
import ge.msda.commons.verssionmanagement.constants.CommonConstants;
import ge.msda.commons.verssionmanagement.entities.EntityWithArchive;

import java.util.Date;

public class Tools {

    /*public static <T> T getSingleInstanceOfCurrentVersion(List<T> currentVersion) throws ResponseObject {
        if (currentVersion == null || currentVersion.size() == 0) {
            ResponseError error = ErrorService.getError("", null);//FIXME //არ არსებობს ან ძველი ვერსიის რედაქტირებას ცდილობთ.
            throw ResponseObject.createFailedResponse(error);
        } else if (currentVersion.size() > 1) {
            ResponseError error = ErrorService.getError("", null);//FIXME //მიმდინარე ვერსიის 1ზე მეტი ჩანაწერი მოიძებნა, დაუკავშირდით ადმინისტრატორს.
            throw ResponseObject.createFailedResponse(error);
        } else {
            return currentVersion.get(0);
        }
    }*/

    public static <T extends EntityWithArchive> void setHistoryFields(T oldVersion, T newVersion, ActionPerformer actionPerformer) {
        newVersion.setActionPerformer(actionPerformer.toString());
        if (actionPerformer.getDate() == null) {
            actionPerformer.setDate(new Date());
        }
        if (oldVersion != null) {
            oldVersion.setToDate(actionPerformer.getDate());
        }
        newVersion.setFromDate(actionPerformer.getDate());
        newVersion.setToDate(CommonConstants.FAR_FUTURE_DATE);
    }
    public static <T extends EntityWithArchive> void setHistoryFields(T newVersion, ActionPerformer actionPerformer) {
        newVersion.setActionPerformer(actionPerformer.toString());
        if (actionPerformer.getDate() == null) {
            actionPerformer.setDate(new Date());
        }
        newVersion.setFromDate(actionPerformer.getDate());
        newVersion.setToDate(CommonConstants.FAR_FUTURE_DATE);
    }
}
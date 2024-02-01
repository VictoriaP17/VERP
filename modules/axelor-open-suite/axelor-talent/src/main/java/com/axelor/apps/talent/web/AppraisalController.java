/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2023 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.talent.web;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.hr.db.Employee;
import com.axelor.apps.hr.db.repo.EmployeeRepository;
import com.axelor.apps.talent.db.Appraisal;
import com.axelor.apps.talent.db.repo.AppraisalRepository;
import com.axelor.apps.talent.service.AppraisalService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class AppraisalController {

  public static final int defaultFunctional = 70;
  public static final int defaultProject = 30;

  public void onNew(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    response.setValue("functionalPercentage", defaultFunctional);
    response.setValue("projectPercentage", defaultProject);
    response.setValue("functionalDivisionsAccum", 100);

    return;
  }

  public String validateCumulativePercentage(int cumulativePercentage) {
    String errorMessage = null;
    if (cumulativePercentage != 100) {
      errorMessage = "Functional subdivision's percentages do not add to 100%";
    }
    return errorMessage;
  }

  public void onSave(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);
    String messageString = validateCumulativePercentage(appraisal.getFunctionalDivisionsAccum());
    if (messageString != null) {
      response.setError(messageString, "Non-valid value");
    }

    return;
  }

  /*
  public void calculateFunctionalPercentage(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);
    int projectPercentage = appraisal.getProjectPercentage();
    if (projectPercentage <= 0 || projectPercentage >= 100) {
      response.setAlert(
          "Project and functional percentages should be greater than 0 and less than 100",
          "Invalid value");
      return;
    }
    int calculatedFunctionalValue = 100 - projectPercentage;
    response.setValue("functionalPercentage", (Integer) calculatedFunctionalValue);
    return;
  }

  public void calculateProjectPercentage(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);
    int functionalPercentage = appraisal.getFunctionalPercentage();
    if (functionalPercentage <= 0 || functionalPercentage >= 100) {
      response.setAlert(
          "Project and functional percentages should be greater than 0 and less than 100",
          "Invalid value");
      return;
    }
    int calculatedProjectValue = 100 - functionalPercentage;
    response.setValue("projectPercentage", (Integer) calculatedProjectValue);
    return;
  }
  */

  public void lockUnlockDivision0(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck0();
    if (divisionState) {
      response.setAttr("functionalDivisionName0", "readonly", false);
      response.setAttr("functionalDivisionValue0", "readonly", false);
    } else {
      response.setAttr("functionalDivisionName0", "readonly", true);
      response.setValue("functionalDivisionName0", "");
      response.setAttr("functionalDivisionValue0", "readonly", true);
      response.setValue("functionalDivisionValue0", 0);
    }
    return;
  }

  public void lockUnlockDivision1(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck1();
    if (divisionState) {
      response.setValue("functionalDivisionName1", "");
      response.setAttr("functionalDivisionName1", "readonly", false);
      response.setValue("functionalDivisionValue1", 0);
      response.setAttr("functionalDivisionValue1", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName1", "readonly", true);
      response.setAttr("functionalDivisionValue1", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision2(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck2();
    if (divisionState) {
      response.setValue("functionalDivisionName2", "");
      response.setAttr("functionalDivisionName2", "readonly", false);
      response.setValue("functionalDivisionValue2", 0);
      response.setAttr("functionalDivisionValue2", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName2", "readonly", true);
      response.setAttr("functionalDivisionValue2", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision3(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck3();
    if (divisionState) {
      response.setValue("functionalDivisionName3", "");
      response.setAttr("functionalDivisionName3", "readonly", false);
      response.setValue("functionalDivisionValue3", 0);
      response.setAttr("functionalDivisionValue3", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName3", "readonly", true);
      response.setAttr("functionalDivisionValue3", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision4(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck4();
    if (divisionState) {
      response.setValue("functionalDivisionName4", "");
      response.setAttr("functionalDivisionName4", "readonly", false);
      response.setValue("functionalDivisionValue4", 0);
      response.setAttr("functionalDivisionValue4", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName4", "readonly", true);
      response.setAttr("functionalDivisionValue4", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision5(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck5();
    if (divisionState) {
      response.setValue("functionalDivisionName5", "");
      response.setAttr("functionalDivisionName5", "readonly", false);
      response.setValue("functionalDivisionValue5", 0);
      response.setAttr("functionalDivisionValue5", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName5", "readonly", true);
      response.setAttr("functionalDivisionValue5", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision6(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck6();
    if (divisionState) {
      response.setValue("functionalDivisionName6", "");
      response.setAttr("functionalDivisionName6", "readonly", false);
      response.setValue("functionalDivisionValue6", 0);
      response.setAttr("functionalDivisionValue6", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName6", "readonly", true);
      response.setAttr("functionalDivisionValue6", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision7(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck7();
    if (divisionState) {
      response.setValue("functionalDivisionName7", "");
      response.setAttr("functionalDivisionName7", "readonly", false);
      response.setValue("functionalDivisionValue7", 0);
      response.setAttr("functionalDivisionValue7", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName7", "readonly", true);
      response.setAttr("functionalDivisionValue7", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision8(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck8();
    if (divisionState) {
      response.setValue("functionalDivisionName8", "");
      response.setAttr("functionalDivisionName8", "readonly", false);
      response.setValue("functionalDivisionValue8", 0);
      response.setAttr("functionalDivisionValue8", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName8", "readonly", true);
      response.setAttr("functionalDivisionValue8", "readonly", true);
    }
    return;
  }

  public void lockUnlockDivision9(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    boolean divisionState = appraisal.getFunctionalDivisionCheck9();
    if (divisionState) {
      response.setValue("functionalDivisionName9", "");
      response.setAttr("functionalDivisionName9", "readonly", false);
      response.setValue("functionalDivisionValue9", 0);
      response.setAttr("functionalDivisionValue9", "readonly", false);
      return;
    } else {
      response.setAttr("functionalDivisionName9", "readonly", true);
      response.setAttr("functionalDivisionValue9", "readonly", true);
    }
    return;
  }

  public void calculateCumulativeSubpercentage(ActionRequest request, ActionResponse response) {
    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    int cumulativeSubpercentage = 0;
    List<Boolean> checkedDivisionsList = new ArrayList<>();
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck0());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck1());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck2());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck3());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck4());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck5());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck6());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck7());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck8());
    checkedDivisionsList.add(appraisal.getFunctionalDivisionCheck9());

    List<Integer> valuesList = new ArrayList<>();
    valuesList.add(appraisal.getFunctionalDivisionValue0());
    valuesList.add(appraisal.getFunctionalDivisionValue1());
    valuesList.add(appraisal.getFunctionalDivisionValue2());
    valuesList.add(appraisal.getFunctionalDivisionValue3());
    valuesList.add(appraisal.getFunctionalDivisionValue4());
    valuesList.add(appraisal.getFunctionalDivisionValue5());
    valuesList.add(appraisal.getFunctionalDivisionValue6());
    valuesList.add(appraisal.getFunctionalDivisionValue7());
    valuesList.add(appraisal.getFunctionalDivisionValue8());
    valuesList.add(appraisal.getFunctionalDivisionValue9());

    for (int i = 0; i < 10; i++) {
      if (checkedDivisionsList.get(i)) {
        cumulativeSubpercentage += valuesList.get(i);
      }
    }

    response.setValue("functionalDivisionsAccum", cumulativeSubpercentage);
    return;
  }

  public void send(ActionRequest request, ActionResponse response) {

    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    try {
      appraisal = Beans.get(AppraisalRepository.class).find(appraisal.getId());

      String messageString = validateCumulativePercentage(appraisal.getFunctionalDivisionsAccum());
      if (messageString != null) {
        response.setError(messageString, "Non-valid value");
        return;
      }

      Beans.get(AppraisalService.class).send(appraisal);

      response.setReload(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void realize(ActionRequest request, ActionResponse response) {

    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    try {
      appraisal = Beans.get(AppraisalRepository.class).find(appraisal.getId());

      Beans.get(AppraisalService.class).realize(appraisal);

      response.setReload(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void cancel(ActionRequest request, ActionResponse response) {

    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    try {
      appraisal = Beans.get(AppraisalRepository.class).find(appraisal.getId());

      Beans.get(AppraisalService.class).cancel(appraisal);

      response.setReload(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void draft(ActionRequest request, ActionResponse response) {

    Appraisal appraisal = request.getContext().asType(Appraisal.class);

    try {
      appraisal = Beans.get(AppraisalRepository.class).find(appraisal.getId());

      Beans.get(AppraisalService.class).draft(appraisal);

      response.setReload(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void createAppraisals(ActionRequest request, ActionResponse response) {

    try {
      Context context = request.getContext();

      Set<Map<String, Object>> employeeSet = new HashSet<Map<String, Object>>();

      employeeSet.addAll((Collection<? extends Map<String, Object>>) context.get("employeeSet"));

      Set<Employee> employees = new HashSet<Employee>();

      EmployeeRepository employeeRepo = Beans.get(EmployeeRepository.class);

      for (Map<String, Object> emp : employeeSet) {
        Long empId = Long.parseLong(emp.get("id").toString());
        employees.add(employeeRepo.find(empId));
      }

      Long templateId = Long.parseLong(context.get("templateId").toString());

      Appraisal appraisalTemplate = Beans.get(AppraisalRepository.class).find(templateId);

      Boolean send = (Boolean) context.get("sendAppraisals");

      Set<Long> createdIds =
          Beans.get(AppraisalService.class).createAppraisals(appraisalTemplate, employees, send);

      response.setView(
          ActionView.define(I18n.get("Appraisal"))
              .model(Appraisal.class.getName())
              .add("grid", "appraisal-grid")
              .add("form", "appraisal-form")
              .param("search-filters", "appraisal-fitlers")
              .domain("self.id in :createdIds")
              .context("createdIds", createdIds)
              .map());

      response.setCanClose(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}

package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.isCohortWithExactlyIds;

public class EncounterWithCodedObsCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

    @Autowired @Qualifier("encounterService")
    private EncounterService encounterService;

    @Autowired @Qualifier("conceptService")
    private ConceptService conceptService;

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluateIncludingValue() throws Exception {
        EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
        cd.addEncounterType(encounterService.getEncounterType(2));
        cd.setConcept(conceptService.getConcept(21));
        cd.addIncludeCodedValue(conceptService.getConcept(8));

        EvaluatedCohort result = cohortDefinitionService.evaluate(cd, new EvaluationContext());
        assertThat(result, isCohortWithExactlyIds(7));
    }

    @Test
    public void testEvaluateExcludingValue() throws Exception {
        EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
        cd.addEncounterType(encounterService.getEncounterType(1));
        cd.setConcept(conceptService.getConcept(21));
        cd.addExcludeCodedValue(conceptService.getConcept(8));

        EvaluatedCohort result = cohortDefinitionService.evaluate(cd, new EvaluationContext());
        assertThat(result, isCohortWithExactlyIds(7)); // TODO use a better test dataset
    }

    @Test
    public void testEvaluateNullValue() throws Exception {
        EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
        cd.addEncounterType(encounterService.getEncounterType(6));
        cd.setConcept(conceptService.getConcept(21));
        cd.setIncludeNoObsValue(true);

        EvaluatedCohort result = cohortDefinitionService.evaluate(cd, new EvaluationContext());
        assertThat(result, isCohortWithExactlyIds(20, 21, 22, 23, 24)); // TODO use a better test dataset
    }

}

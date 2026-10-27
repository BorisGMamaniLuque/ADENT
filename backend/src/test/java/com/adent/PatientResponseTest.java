package com.adent;
import com.adent.patient.api.PatientResponse; import com.adent.patient.domain.Patient; import org.junit.jupiter.api.Test; import java.time.LocalDate; import static org.junit.jupiter.api.Assertions.*;
class PatientResponseTest {
 @Test void calculatesAgeInsteadOfStoringIt(){Patient p=new Patient();p.setFirstNames("Ana");p.setLastNames("Demo");p.setHistoryNumber("HC-000001");p.setBirthDate(LocalDate.now().minusYears(20));PatientResponse r=PatientResponse.from(p);assertEquals(20,r.age());}
}

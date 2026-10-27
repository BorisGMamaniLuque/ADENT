package com.adent.shared.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Datos estrictamente ficticios para demostraciones locales. No se ejecuta en producción. */
final class DemoClinicSeeder {
    private DemoClinicSeeder() {}
    private static UUID id(String key) { return UUID.nameUUIDFromBytes(("adent-demo-2026:" + key).getBytes(StandardCharsets.UTF_8)); }

    static void populate(JdbcTemplate db, PasswordEncoder encoder, UUID tenantId, String ownerUsername) {
        UUID branch = id("branch");
        UUID floor = id("floor");
        db.update("INSERT INTO branches(id,tenant_id,name,address) VALUES (?,?,?,?) ON CONFLICT(id) DO NOTHING",
                branch,tenantId,"Sede Dental Central - DEMO","Tacna, Perú (ubicación ficticia)");
        db.update("INSERT INTO floors(id,tenant_id,branch_id,name,floor_number) VALUES (?,?,?,?,?) ON CONFLICT(id) DO NOTHING",
                floor,tenantId,branch,"Planta principal",1);
        for(int n=1;n<=10;n++) {
            db.update("INSERT INTO rooms(id,tenant_id,floor_id,name,code) VALUES (?,?,?,?,?) ON CONFLICT(id) DO NOTHING",
                    id("room-"+n),tenantId,floor,"Consultorio "+n,"C"+n);
        }
        String[] names={"Ana","Luis","María","Carlos","Rosa","Jorge","Patricia","Daniel","Elena","Miguel"};
        String[] last={"Torres","Rodríguez","Gómez","Mendoza","Mejía","Salazar","Rojas","Ruiz","Castillo","Flores"};
        String[] specialties={"Odontología general","Ortodoncia","Endodoncia","Cirugía bucal","Odontopediatría","Periodoncia","Rehabilitación oral","Implantología","Estética dental","Odontología integral"};
        for(int n=1;n<=10;n++) {
            db.update("INSERT INTO staff(id,tenant_id,document_type,document_number,first_names,last_names,specialty,email) VALUES (?,?,?,?,?,?,?,?) ON CONFLICT(id) DO NOTHING",
                    id("doctor-"+n),tenantId,"DNI",String.format("90%06d",n),names[n-1],last[n-1],specialties[n-1],"doctor"+n+"@demo.adent.local");
        }
        String[][] accounts={{"gerente","ADMIN"},{"administrador","ADMIN"},{"recepcion1","RECEPTION"},{"recepcion2","RECEPTION"},
                {"odontologo1","DENTIST"},{"odontologo2","DENTIST"},{"odontologo3","DENTIST"},{"odontologo4","DENTIST"},{"odontologo5","DENTIST"}};
        for(int n=0;n<accounts.length;n++) {
            String user=accounts[n][0], role=accounts[n][1];
            UUID userId=id("user-"+user), membershipId=id("membership-"+user);
            db.update("INSERT INTO users(id,username,email,password_hash) VALUES (?,?,?,?) ON CONFLICT(username) DO NOTHING",
                    userId,user,user+"@demo.adent.local",encoder.encode("Demo2026!"));
            UUID actualUser=db.queryForObject("SELECT id FROM users WHERE lower(username)=lower(?)",UUID.class,user);
            UUID staffId=role.equals("DENTIST") ? id("doctor-"+(n-3)) : null;
            db.update("INSERT INTO tenant_memberships(id,tenant_id,user_id,staff_id,default_membership) VALUES (?,?,?,?,true) ON CONFLICT(tenant_id,user_id) DO NOTHING",
                    membershipId,tenantId,actualUser,staffId);
            db.update("INSERT INTO membership_roles(membership_id,role_id) SELECT tm.id,r.id FROM tenant_memberships tm CROSS JOIN roles r WHERE tm.tenant_id=? AND tm.user_id=? AND r.code=? ON CONFLICT(membership_id,role_id) DO NOTHING",
                    tenantId,actualUser,role);
        }
        String[] first={"Lucía","Diego","Sofía","Andrés","Valeria","Ricardo","Camila","Javier","Natalia","Pedro",
                "Elisa","Mateo","Claudia","Felipe","Paola","Hugo","Teresa","Bruno","Daniela","Iván"};
        String[] surnames={"Quispe","Flores","Vargas","Huamán","Mamani","Paredes","Rivera","Condori","Aguilar","López"};
        for(int n=1;n<=50;n++) {
            UUID patient=id("patient-"+n);
            db.update("INSERT INTO patients(id,tenant_id,history_number,document_type,document_number,first_names,last_names,nationality,birth_date,phone,email,address) VALUES (?,?,?,?,?,?,?,?,(DATE '1980-01-01' + (? * 181)),?,?,?) ON CONFLICT(id) DO NOTHING",
                    patient,tenantId,String.format("HC-DEMO-%04d",n),"DNI",String.format("80%06d",n),first[(n-1)%first.length],surnames[(n-1)%surnames.length],"Peruana",n,"900"+String.format("%06d",n),"paciente"+n+"@demo.adent.local","Tacna - Dirección ficticia #"+n);
            db.update("INSERT INTO clinical_records(id,tenant_id,patient_id,summary) VALUES (?,?,?,?) ON CONFLICT(patient_id) DO NOTHING",
                    id("clinical-"+n),tenantId,patient,"Expediente ficticio para demostración académica");
            db.update("INSERT INTO appointments(id,tenant_id,patient_id,staff_id,room_id,starts_at,ends_at,reason,status) VALUES (?,?,?,?,?,(current_date + INTERVAL '8 hours' + (? * INTERVAL '20 minutes')),(current_date + INTERVAL '8 hours' + (? * INTERVAL '20 minutes') + INTERVAL '30 minutes'),?,?) ON CONFLICT(id) DO NOTHING",
                    id("appointment-"+n),tenantId,patient,id("doctor-"+(((n-1)%10)+1)),id("room-"+(((n-1)%10)+1)),n,n,"Evaluación odontológica (DEMO)","SCHEDULED");
        }
        // No se sobrescriben contraseñas ni registros existentes.
    }
}

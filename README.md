# Progetto Capstone: Piattaforma di Gestione Documentale con Firma Elettronica

## Autore
**Lorenzo Lione** – Corso Full Stack Developer – FS0824IT-A

## Sinossi
Il progetto prevede lo sviluppo di un'applicazione full-stack per la gestione e la firma elettronica di documenti in contesti organizzativi. L'obiettivo è automatizzare il processo di raccolta, validazione, firma e archiviazione dei documenti, garantendo conformità normativa e sicurezza.

Il sistema consente al personale autorizzato di inserire i dati degli utenti, generare documenti PDF e inviarli per la firma elettronica avanzata tramite OTP via SMS, utilizzando un provider certificato (es. Namiral) conforme al Regolamento eIDAS (UE 910/2014). Include meccanismi di validazione automatica e possibilità di integrazione nei sistemi gestionali esistenti.

## Tecnologie Utilizzate
- **Frontend**: React, Redux, TypeScript
- **Backend**: Java, Spring Boot, JPA
- **Autenticazione**: JWT
- **Database**: PostgreSQL
- **Firma elettronica**: integrazione API (es. Namiral)
- **Notifiche**: SMTP / API terze parti
- **Deployment**: Docker, cloud AWS o equivalenti

## Flusso di Funzionamento
1. Inserimento dati utenti tramite modulo digitale
2. Generazione PDF lato backend
3. Invio per firma elettronica via OTP/SMS
4. Validazione automatica dei dati
5. Notifica alla segreteria
6. Verifica e archiviazione del documento

## Obiettivi del Progetto
- Digitalizzazione dei flussi documentali
- Validità legale garantita (eIDAS)
- Tracciabilità delle operazioni
- Sicurezza e conformità normativa
- Possibile adozione da parte di un cliente reale

## Valore Aggiunto
- **Automazione e riduzione degli errori**: validazioni avanzate su CF, email, telefono, appartenenza aziendale
- **Conformità legale**: firma elettronica qualificata, audit trail
- **Efficienza operativa**: ottimizzazione dei tempi e riduzione dei costi
- **Scalabilità**: architettura cloud-native modulare
- **Tracciabilità e reportistica**: accesso sicuro e insight sui documenti
- **Integrazione**: compatibilità con ERP, CRM e altri gestionali tramite API

## Conclusione
Il progetto rappresenta una soluzione tecnologicamente avanzata e giuridicamente conforme per la gestione dei documenti digitali. L'integrazione di tecnologie moderne con requisiti legali stringenti lo rende adatto all'adozione aziendale e alla scalabilità su larga scala.
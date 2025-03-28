select templates.subject as EnvelopName,
       signedByUsers.email as SignerEmail,
       signedByUsers.name as SignerName,
       signedByUsers.surname as SignerSurname,
       signedByUsers.cellphone as SignerCellPhone,
       createdByUsers.email as CreatorEmail,
       createdByUsers.name as CreatorName,
       createdByUsers.surname as CreatorSurname,
       createdByUsers.cellphone as CreatorCellPhone,
       templates.subject as EmailSubject,
       templates.template_json AS TemplateJson
from sign_requests as requests
join users as signedByUsers on requests.signed_by_app_user_id = signedByUsers.id
join users as createdByUsers on requests.created_by_app_user_id = createdByUsers.id
join doc_template as templates on requests.doc_template_id = templates.id
where requests.id = {{signRequest.Id}};
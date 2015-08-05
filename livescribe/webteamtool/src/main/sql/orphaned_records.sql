
-- Orphaned Page records
select * from page where document_id not in (select document_id from document);

-- Orphaned Session records
select * from `session` where document_id not in (select document_id from document);

-- Orphaned Template records
select * from template where document_id not in (select document_id from document);

-- Orphaned Audio records
select * from audio where session_id not in (select session_id from `session`);

-- Orphaned TimeMap records
select * from timemap where session_id not in (select session_id from `session`) and page_id not in (select page_id from page);

-- Orphaned Sync_Times_Info records.
-- select * from sync_times_info where 
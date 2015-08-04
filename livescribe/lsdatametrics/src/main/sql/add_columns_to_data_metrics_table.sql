-- TA10023:  Save 'message_id' from SQS message to 'data_metric' table.
alter table data_metric add column message_id varchar(64);

-- US6350:  Update data metrics spec to include generic devices.
alter table data_metric add column device_action varchar(64);
alter table data_metric change column pen_serial_num device_id varchar(64);
alter table data_metric change column pen_type device_type varchar(64);

alter table data_metrics add column app_class varchar(64);
alter table data_metrics add column button_class varchar(64);
alter table data_metrics add column page_number varchar(64);
alter table data_metrics add column notebook_guid varchar(64);
alter table data_metrics add column is_docked boolean;


create table data_metric (
	id					int			primary key		auto_increment,
	start_metric_id		int,
	message_id			varchar(64),
	metric_type			varchar(64),
	metric_version		varchar(32),
	is_test_data		boolean,
	pen_serial_num		varchar(64),
	pen_type			varchar(32),
	metric_sent			timestamp	default '0000-00-00 00:00:00',
	created				timestamp	default '0000-00-00 00:00:00'
);

CREATE EXTENSION pljvm;

CREATE OR REPLACE FUNCTION pljvm_log100(d float8) RETURNS float8 AS $$
org.postgresql.plj.test.Math.log10
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_bool(b bool) RETURNS bool AS $$
org.postgresql.plj.test.Bool.valueOf
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_add(i int2, j int2) RETURNS int2 AS $$
org.postgresql.plj.test.Int.add
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_add(i int4, j int4) RETURNS int4 AS $$
org.postgresql.plj.test.Int.add
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_add(i int8, j int8) RETURNS int8 AS $$
org.postgresql.plj.test.Int.add
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_add(f float4, g float4) RETURNS float4 AS $$
org.postgresql.plj.test.Math.add
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_add(f float8, g float8) RETURNS float8 AS $$
org.postgresql.plj.test.Math.add
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_numeric(n1 numeric, n2 numeric) RETURNS numeric AS $$
org.postgresql.plj.test.Math.add
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_timestamp(t timestamp) RETURNS timestamp AS $$
org.postgresql.plj.test.Timestamp.valueOf
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_timestamptz(t timestamptz) RETURNS timestamptz AS $$
org.postgresql.plj.test.Timestamp.valueOf
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_text(arg1 varchar, arg2 varchar) RETURNS varchar AS $$
org.postgresql.plj.test.Text.concat
$$ LANGUAGE pljvm;

CREATE OR REPLACE FUNCTION pljvm_add_arr(arg1 int4[], int4) returns int4[] as $$
org.postgresql.plj.test.Int.add
$$ language 'pljvm';

CREATE OR REPLACE FUNCTION pljvm_add_arr(arg1 int2[], int2) returns int2[] as $$
org.postgresql.plj.test.Int.add
$$ language 'pljvm';

CREATE OR REPLACE FUNCTION pljvm_add_arr(arg1 int8[], int4) returns int8[] as $$
org.postgresql.plj.test.Int.add
$$ language 'pljvm';

CREATE OR REPLACE FUNCTION pljvm_bool_arr_xor(arg1 boolean[], x boolean) returns boolean[] as $$
org.postgresql.plj.test.Bool.xor
$$ language 'pljvm';

CREATE OR REPLACE FUNCTION pljvm_exception() returns void as $$
org.postgresql.plj.test.Error.nullPointer
$$ language 'pljvm';

CREATE OR REPLACE FUNCTION pljvm_log(msg text) returns void as $$
org.postgresql.plj.test.Log.log
$$ language 'pljvm';


select pljvm_log100(100);
select pljvm_bool(true);
select pljvm_add(1::int2,2::int2);
select pljvm_add(3,4);
select pljvm_add(5::int8,6::int8);
select pljvm_add(1.2::float4,3.4::float4);
select pljvm_add(5.6::float8,7.8::float8);
select pljvm_timestamp('2017-11-24 07:23:20.222432');
select pljvm_timestamptz('2017-11-24 07:23:48.597243-08');
select pljvm_text('hello ', 'world');
select pljvm_add_arr('{1,2,3}'::int2[], 1::int2);
select pljvm_add_arr('{3,4,5}'::int4[], 1);
select pljvm_add_arr('{6,7,8}'::int8[], 1);
select pljvm_bool_arr_xor('{1,0,0,1}'::boolean[],true);
select pljvm_log('hello');


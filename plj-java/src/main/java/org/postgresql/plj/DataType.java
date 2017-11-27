package org.postgresql.plj;

public enum DataType {
  PLJVM_DATA_INT1,  // 1-byte integer
  PLJVM_DATA_INT2,  // 2-byte integer
  PLJVM_DATA_INT4,  // 4-byte integer
  PLJVM_DATA_INT8,  // 8-byte integer
  PLJVM_DATA_FLOAT4,  // 4-byte float
  PLJVM_DATA_FLOAT8,  // 8-byte float
  PLJVM_DATA_TEXT,  // Text - transferred as a set of bytes of predefined length,
  //        stored as cstring
  PLJVM_DATA_ARRAY,  // Array - array type specification should follow
  PLJVM_DATA_UDT,  // User-defined type, specification to follow
  PLJVM_DATA_BYTEA,  // Arbitrary set of bytes, stored and transferred as length + data
  PLJVM_DATA_INVALID  // Invalid data type
}

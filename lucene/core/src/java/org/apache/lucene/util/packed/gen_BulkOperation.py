begin_unit
comment|'#! /usr/bin/env python'
nl|'\n'
nl|'\n'
comment|'# Licensed to the Apache Software Foundation (ASF) under one or more'
nl|'\n'
comment|'# contributor license agreements.  See the NOTICE file distributed with'
nl|'\n'
comment|'# this work for additional information regarding copyright ownership.'
nl|'\n'
comment|'# The ASF licenses this file to You under the Apache License, Version 2.0'
nl|'\n'
comment|'# (the "License"); you may not use this file except in compliance with'
nl|'\n'
comment|'# the License.  You may obtain a copy of the License at'
nl|'\n'
comment|'#'
nl|'\n'
comment|'#     http://www.apache.org/licenses/LICENSE-2.0'
nl|'\n'
comment|'#'
nl|'\n'
comment|'# Unless required by applicable law or agreed to in writing, software'
nl|'\n'
comment|'# distributed under the License is distributed on an "AS IS" BASIS,'
nl|'\n'
comment|'# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.'
nl|'\n'
comment|'# See the License for the specific language governing permissions and'
nl|'\n'
comment|'# limitations under the License.'
nl|'\n'
nl|'\n'
name|'from'
name|'fractions'
name|'import'
name|'gcd'
newline|'\n'
nl|'\n'
string|'"""Code generation for bulk operations"""'
newline|'\n'
nl|'\n'
DECL|variable|PACKED_64_SINGLE_BLOCK_BPV
name|'PACKED_64_SINGLE_BLOCK_BPV'
op|'='
op|'['
number|'1'
op|','
number|'2'
op|','
number|'3'
op|','
number|'4'
op|','
number|'5'
op|','
number|'6'
op|','
number|'7'
op|','
number|'8'
op|','
number|'9'
op|','
number|'10'
op|','
number|'12'
op|','
number|'16'
op|','
number|'21'
op|','
number|'32'
op|']'
newline|'\n'
DECL|variable|OUTPUT_FILE
name|'OUTPUT_FILE'
op|'='
string|'"BulkOperation.java"'
newline|'\n'
name|'HEADER'
op|'='
string|'"""// This file has been automatically generated, DO NOT EDIT\n\npackage org.apache.lucene.util.packed;\n\n/*\n * Licensed to the Apache Software Foundation (ASF) under one or more\n * contributor license agreements.  See the NOTICE file distributed with\n * this work for additional information regarding copyright ownership.\n * The ASF licenses this file to You under the Apache License, Version 2.0\n * (the "License"); you may not use this file except in compliance with\n * the License.  You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an "AS IS" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n\nimport java.nio.IntBuffer;\nimport java.nio.LongBuffer;\nimport java.util.EnumMap;\n\n/**\n * Efficient sequential read/write of packed integers.\n */\nabstract class BulkOperation implements PackedInts.Decoder, PackedInts.Encoder {\n\n  static final EnumMap<PackedInts.Format, BulkOperation[]> BULK_OPERATIONS = new EnumMap<PackedInts.Format, BulkOperation[]>(PackedInts.Format.class);\n\n  public static BulkOperation of(PackedInts.Format format, int bitsPerValue) {\n    assert bitsPerValue > 0 && bitsPerValue <= 64;\n    BulkOperation[] ops = BULK_OPERATIONS.get(format);\n    if (ops == null || ops[bitsPerValue] == null) {\n      throw new IllegalArgumentException("format: " + format + ", bitsPerValue: " + bitsPerValue);\n    }\n    return ops[bitsPerValue];\n  }\n\n  /**\n   * For every number of bits per value, there is a minimum number of\n   * blocks (b) / values (v) you need to write in order to reach the next block\n   * boundary:\n   *  - 16 bits per value -> b=1, v=4\n   *  - 24 bits per value -> b=3, v=8\n   *  - 50 bits per value -> b=25, v=32\n   *  - 63 bits per value -> b=63, v = 64\n   *  - ...\n   *\n   * A bulk read consists in copying <code>iterations*v</code> values that are\n   * contained in <code>iterations*b</code> blocks into a <code>long[]</code>\n   * (higher values of <code>iterations</code> are likely to yield a better\n   * throughput) => this requires n * (b + v) longs in memory.\n   *\n   * This method computes <code>iterations</code> as\n   * <code>ramBudget / (8 * (b + v))</code> (since a long is 8 bytes).\n   */\n  public final int computeIterations(int valueCount, int ramBudget) {\n    final int iterations = (ramBudget >>> 3) / (blocks() + values());\n    if (iterations == 0) {\n      // at least 1\n      return 1;\n    } else if ((iterations - 1) * blocks() >= valueCount) {\n      // don\'t allocate for more than the size of the reader\n      return (int) Math.ceil((double) valueCount / values());\n    } else {\n      return iterations;\n    }\n  }\n\n"""'
newline|'\n'
nl|'\n'
DECL|variable|FOOTER
name|'FOOTER'
op|'='
string|'"}"'
newline|'\n'
nl|'\n'
DECL|function|casts
name|'def'
name|'casts'
op|'('
name|'typ'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'cast_start'
op|'='
string|'"(%s) ("'
op|'%'
name|'typ'
newline|'\n'
name|'cast_end'
op|'='
string|'")"'
newline|'\n'
name|'if'
name|'typ'
op|'=='
string|'"long"'
op|':'
newline|'\n'
indent|'    '
name|'cast_start'
op|'='
string|'""'
newline|'\n'
name|'cast_end'
op|'='
string|'""'
newline|'\n'
dedent|''
name|'return'
name|'cast_start'
op|','
name|'cast_end'
newline|'\n'
nl|'\n'
DECL|function|masks
dedent|''
name|'def'
name|'masks'
op|'('
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'bits'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'""'
op|','
string|'""'
newline|'\n'
dedent|''
name|'return'
string|'"("'
op|','
string|'" & %sL)"'
op|'%'
op|'('
name|'hex'
op|'('
op|'('
number|'1'
op|'<<'
name|'bits'
op|')'
op|'-'
number|'1'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|get_type
dedent|''
name|'def'
name|'get_type'
op|'('
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'bits'
op|'=='
number|'8'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"byte"'
newline|'\n'
dedent|''
name|'elif'
name|'bits'
op|'=='
number|'16'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"short"'
newline|'\n'
dedent|''
name|'elif'
name|'bits'
op|'=='
number|'32'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"int"'
newline|'\n'
dedent|''
name|'elif'
name|'bits'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"long"'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'assert'
name|'False'
newline|'\n'
nl|'\n'
DECL|function|packed64singleblock
dedent|''
dedent|''
name|'def'
name|'packed64singleblock'
op|'('
name|'bpv'
op|','
name|'f'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'values'
op|'='
number|'64'
op|'/'
name|'bpv'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"\\n  static final class Packed64SingleBlockBulkOperation%d extends BulkOperation {\\n\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public int blocks() {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      return 1;\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"     }\\n\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public int values() {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      return %d;\\n"'
op|'%'
name|'values'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
name|'p64sb_decode'
op|'('
name|'bpv'
op|','
number|'32'
op|')'
newline|'\n'
name|'p64sb_decode'
op|'('
name|'bpv'
op|','
number|'64'
op|')'
newline|'\n'
name|'p64sb_encode'
op|'('
name|'bpv'
op|','
number|'32'
op|')'
newline|'\n'
name|'p64sb_encode'
op|'('
name|'bpv'
op|','
number|'64'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n"'
op|')'
newline|'\n'
nl|'\n'
DECL|function|p64sb_decode
dedent|''
name|'def'
name|'p64sb_decode'
op|'('
name|'bpv'
op|','
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'values'
op|'='
number|'64'
op|'/'
name|'bpv'
newline|'\n'
name|'typ'
op|'='
name|'get_type'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'buf'
op|'='
name|'typ'
op|'.'
name|'title'
op|'('
op|')'
op|'+'
string|'"Buffer"'
newline|'\n'
name|'cast_start'
op|','
name|'cast_end'
op|'='
name|'casts'
op|'('
name|'typ'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public void decode(LongBuffer blocks, %s values, int iterations) {\\n"'
op|'%'
name|'buf'
op|')'
newline|'\n'
name|'if'
name|'bits'
op|'<'
name|'bpv'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      throw new UnsupportedOperationException();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
name|'return'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert blocks.position() + iterations * blocks() <= blocks.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert values.position() + iterations * values() <= values.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      for (int i = 0; i < iterations; ++i) {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"        final long block = blocks.get();\\n"'
op|')'
newline|'\n'
name|'mask'
op|'='
op|'('
number|'1'
op|'<<'
name|'bpv'
op|')'
op|'-'
number|'1'
newline|'\n'
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
name|'values'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'block_offset'
op|'='
name|'i'
op|'/'
name|'values'
newline|'\n'
name|'offset_in_block'
op|'='
name|'i'
op|'%'
name|'values'
newline|'\n'
name|'if'
name|'i'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%sblock & %dL%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'i'
op|'=='
name|'values'
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%sblock >>> %d%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'i'
op|'*'
name|'bpv'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%s(block >>> %d) & %dL%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'i'
op|'*'
name|'bpv'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
DECL|function|p64sb_encode
dedent|''
name|'def'
name|'p64sb_encode'
op|'('
name|'bpv'
op|','
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'values'
op|'='
number|'64'
op|'/'
name|'bpv'
newline|'\n'
name|'typ'
op|'='
name|'get_type'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'buf'
op|'='
name|'typ'
op|'.'
name|'title'
op|'('
op|')'
op|'+'
string|'"Buffer"'
newline|'\n'
name|'mask_start'
op|','
name|'mask_end'
op|'='
name|'masks'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public void encode(%s values, LongBuffer blocks, int iterations) {\\n"'
op|'%'
name|'buf'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert blocks.position() + iterations * blocks() <= blocks.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert values.position() + iterations * values() <= values.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      for (int i = 0; i < iterations; ++i) {\\n"'
op|')'
newline|'\n'
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
name|'values'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'block_offset'
op|'='
name|'i'
op|'/'
name|'values'
newline|'\n'
name|'offset_in_block'
op|'='
name|'i'
op|'%'
name|'values'
newline|'\n'
name|'if'
name|'i'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        blocks.put(%svalues.get()%s"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'" | (%svalues.get()%s << %d)"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|','
name|'i'
op|'*'
name|'bpv'
op|')'
op|')'
newline|'\n'
name|'if'
name|'i'
op|'=='
name|'values'
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'        '
name|'f'
op|'.'
name|'write'
op|'('
string|'");\\n"'
op|')'
newline|'\n'
dedent|''
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
DECL|function|packed64
dedent|''
name|'def'
name|'packed64'
op|'('
name|'bpv'
op|','
name|'f'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'blocks'
op|'='
name|'bpv'
newline|'\n'
name|'values'
op|'='
name|'blocks'
op|'*'
number|'64'
op|'/'
name|'bpv'
newline|'\n'
name|'while'
name|'blocks'
op|'%'
number|'2'
op|'=='
number|'0'
name|'and'
name|'values'
op|'%'
number|'2'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'blocks'
op|'/='
number|'2'
newline|'\n'
name|'values'
op|'/='
number|'2'
newline|'\n'
dedent|''
name|'assert'
name|'values'
op|'*'
name|'bpv'
op|'=='
number|'64'
op|'*'
name|'blocks'
op|','
string|'"%d values, %d blocks, %d bits per value"'
op|'%'
op|'('
name|'values'
op|','
name|'blocks'
op|','
name|'bpv'
op|')'
newline|'\n'
name|'mask'
op|'='
op|'('
number|'1'
op|'<<'
name|'bpv'
op|')'
op|'-'
number|'1'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  static final class Packed64BulkOperation%d extends BulkOperation {\\n\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public int blocks() {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      return %d;\\n"'
op|'%'
name|'blocks'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public int values() {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      return %d;\\n"'
op|'%'
name|'values'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'bpv'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"""    public void decode(LongBuffer blocks, LongBuffer values, int iterations) {\n      final int originalLimit = blocks.limit();\n      blocks.limit(blocks.position() + iterations * blocks());\n      values.put(blocks);\n      blocks.limit(originalLimit);\n    }\n\n    public void decode(LongBuffer blocks, IntBuffer values, int iterations) {\n      throw new UnsupportedOperationException();\n    }\n\n    public void encode(LongBuffer values, LongBuffer blocks, int iterations) {\n      final int originalLimit = values.limit();\n      values.limit(values.position() + iterations * values());\n      blocks.put(values);\n      values.limit(originalLimit);\n    }\n\n    public void encode(IntBuffer values, LongBuffer blocks, int iterations) {\n      for (int i = values.position(), end = values.position() + iterations, j = blocks.position(); i < end; ++i, ++j) {\n        blocks.put(j, values.get(i));\n      }\n    }\n  }\n"""'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'p64_decode'
op|'('
name|'bpv'
op|','
number|'32'
op|','
name|'values'
op|')'
newline|'\n'
name|'p64_decode'
op|'('
name|'bpv'
op|','
number|'64'
op|','
name|'values'
op|')'
newline|'\n'
name|'p64_encode'
op|'('
name|'bpv'
op|','
number|'32'
op|','
name|'values'
op|')'
newline|'\n'
name|'p64_encode'
op|'('
name|'bpv'
op|','
number|'64'
op|','
name|'values'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n"'
op|')'
newline|'\n'
nl|'\n'
DECL|function|p64_decode
dedent|''
dedent|''
name|'def'
name|'p64_decode'
op|'('
name|'bpv'
op|','
name|'bits'
op|','
name|'values'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'typ'
op|'='
name|'get_type'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'buf'
op|'='
name|'typ'
op|'.'
name|'title'
op|'('
op|')'
op|'+'
string|'"Buffer"'
newline|'\n'
name|'cast_start'
op|','
name|'cast_end'
op|'='
name|'casts'
op|'('
name|'typ'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public void decode(LongBuffer blocks, %s values, int iterations) {\\n"'
op|'%'
name|'buf'
op|')'
newline|'\n'
name|'if'
name|'bits'
op|'<'
name|'bpv'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      throw new UnsupportedOperationException();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
name|'return'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert blocks.position() + iterations * blocks() <= blocks.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert values.position() + iterations * values() <= values.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      for (int i = 0; i < iterations; ++i) {\\n"'
op|')'
newline|'\n'
name|'mask'
op|'='
op|'('
number|'1'
op|'<<'
name|'bpv'
op|')'
op|'-'
number|'1'
newline|'\n'
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
number|'0'
op|','
name|'values'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'block_offset'
op|'='
name|'i'
op|'*'
name|'bpv'
op|'/'
number|'64'
newline|'\n'
name|'bit_offset'
op|'='
op|'('
name|'i'
op|'*'
name|'bpv'
op|')'
op|'%'
number|'64'
newline|'\n'
name|'if'
name|'bit_offset'
op|'=='
number|'0'
op|':'
newline|'\n'
comment|'# start of block'
nl|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        final long block%d = blocks.get();\\n"'
op|'%'
name|'block_offset'
op|')'
op|';'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%sblock%d >>> %d%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
number|'64'
op|'-'
name|'bpv'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bit_offset'
op|'+'
name|'bpv'
op|'=='
number|'64'
op|':'
newline|'\n'
comment|'# end of block'
nl|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%sblock%d & %dL%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bit_offset'
op|'+'
name|'bpv'
op|'<'
number|'64'
op|':'
newline|'\n'
comment|'# middle of block'
nl|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%s(block%d >>> %d) & %dL%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
number|'64'
op|'-'
name|'bit_offset'
op|'-'
name|'bpv'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
comment|'# value spans across 2 blocks'
nl|'\n'
indent|'      '
name|'mask1'
op|'='
op|'('
number|'1'
op|'<<'
op|'('
number|'64'
op|'-'
name|'bit_offset'
op|')'
op|')'
op|'-'
number|'1'
newline|'\n'
name|'shift1'
op|'='
name|'bit_offset'
op|'+'
name|'bpv'
op|'-'
number|'64'
newline|'\n'
name|'shift2'
op|'='
number|'64'
op|'-'
name|'shift1'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"        final long block%d = blocks.get();\\n"'
op|'%'
op|'('
name|'block_offset'
op|'+'
number|'1'
op|')'
op|')'
op|';'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values.put(%s((block%d & %dL) << %d) | (block%d >>> %d)%s);\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
name|'mask1'
op|','
name|'shift1'
op|','
name|'block_offset'
op|'+'
number|'1'
op|','
name|'shift2'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
DECL|function|p64_encode
dedent|''
name|'def'
name|'p64_encode'
op|'('
name|'bpv'
op|','
name|'bits'
op|','
name|'values'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'typ'
op|'='
name|'get_type'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'buf'
op|'='
name|'typ'
op|'.'
name|'title'
op|'('
op|')'
op|'+'
string|'"Buffer"'
newline|'\n'
name|'mask_start'
op|','
name|'mask_end'
op|'='
name|'masks'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    public void encode(%s values, LongBuffer blocks, int iterations) {\\n"'
op|'%'
name|'buf'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert blocks.position() + iterations * blocks() <= blocks.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert values.position() + iterations * values() <= values.limit();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      for (int i = 0; i < iterations; ++i) {\\n"'
op|')'
newline|'\n'
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
number|'0'
op|','
name|'values'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'block_offset'
op|'='
name|'i'
op|'*'
name|'bpv'
op|'/'
number|'64'
newline|'\n'
name|'bit_offset'
op|'='
op|'('
name|'i'
op|'*'
name|'bpv'
op|')'
op|'%'
number|'64'
newline|'\n'
name|'if'
name|'bit_offset'
op|'=='
number|'0'
op|':'
newline|'\n'
comment|'# start of block'
nl|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"        blocks.put((%svalues.get()%s << %d)"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|','
number|'64'
op|'-'
name|'bpv'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bit_offset'
op|'+'
name|'bpv'
op|'=='
number|'64'
op|':'
newline|'\n'
comment|'# end of block'
nl|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'" | %svalues.get()%s);\\n"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bit_offset'
op|'+'
name|'bpv'
op|'<'
number|'64'
op|':'
newline|'\n'
comment|'# inside a block'
nl|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'" | (%svalues.get()%s << %d)"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|','
number|'64'
op|'-'
name|'bit_offset'
op|'-'
name|'bpv'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
comment|'# value spans across 2 blocks'
nl|'\n'
indent|'      '
name|'right_bits'
op|'='
name|'bit_offset'
op|'+'
name|'bpv'
op|'-'
number|'64'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'" | (%svalues.get(values.position())%s >>> %d));\\n"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|','
name|'right_bits'
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"        blocks.put((%svalues.get()%s << %d)"'
op|'%'
op|'('
name|'mask_start'
op|','
name|'mask_end'
op|','
number|'64'
op|'-'
name|'right_bits'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
nl|'\n'
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
DECL|variable|p64_bpv
indent|'  '
name|'p64_bpv'
op|'='
op|'['
number|'1'
op|','
number|'2'
op|','
number|'3'
op|','
number|'4'
op|','
number|'5'
op|','
number|'6'
op|','
number|'7'
op|','
number|'8'
op|','
number|'9'
op|','
number|'10'
op|','
number|'12'
op|','
number|'16'
op|','
number|'21'
op|','
number|'32'
op|']'
newline|'\n'
DECL|variable|f
name|'f'
op|'='
name|'open'
op|'('
name|'OUTPUT_FILE'
op|','
string|"'w'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
name|'HEADER'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  static {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    BULK_OPERATIONS.put(PackedInts.Format.PACKED, new BulkOperation[65]);\\n"'
op|')'
newline|'\n'
name|'for'
name|'bpv'
name|'in'
name|'xrange'
op|'('
number|'1'
op|','
number|'65'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    BULK_OPERATIONS.get(PackedInts.Format.PACKED)[%d] = new Packed64BulkOperation%d();\\n"'
op|'%'
op|'('
name|'bpv'
op|','
name|'bpv'
op|')'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"    BULK_OPERATIONS.put(PackedInts.Format.PACKED_SINGLE_BLOCK, new BulkOperation[65]);\\n"'
op|')'
newline|'\n'
name|'for'
name|'bpv'
name|'in'
name|'PACKED_64_SINGLE_BLOCK_BPV'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    BULK_OPERATIONS.get(PackedInts.Format.PACKED_SINGLE_BLOCK)[%d] = new Packed64SingleBlockBulkOperation%d();\\n"'
op|'%'
op|'('
name|'bpv'
op|','
name|'bpv'
op|')'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n"'
op|')'
newline|'\n'
name|'for'
name|'bpv'
name|'in'
name|'xrange'
op|'('
number|'1'
op|','
number|'65'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'packed64'
op|'('
name|'bpv'
op|','
name|'f'
op|')'
newline|'\n'
dedent|''
name|'for'
name|'bpv'
name|'in'
name|'PACKED_64_SINGLE_BLOCK_BPV'
op|':'
newline|'\n'
indent|'    '
name|'packed64singleblock'
op|'('
name|'bpv'
op|','
name|'f'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
name|'FOOTER'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
dedent|''
endmarker|''
end_unit

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
name|'HEADER'
op|'='
string|'"""// This file has been automatically generated, DO NOT EDIT\n\n/*\n * Licensed to the Apache Software Foundation (ASF) under one or more\n * contributor license agreements.  See the NOTICE file distributed with\n * this work for additional information regarding copyright ownership.\n * The ASF licenses this file to You under the Apache License, Version 2.0\n * (the "License"); you may not use this file except in compliance with\n * the License.  You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an "AS IS" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\npackage org.apache.lucene.util.packed;\n\nimport org.apache.lucene.store.DataInput;\nimport org.apache.lucene.util.RamUsageEstimator;\n\nimport java.io.IOException;\nimport java.util.Arrays;\n\n"""'
newline|'\n'
nl|'\n'
DECL|variable|TYPES
name|'TYPES'
op|'='
op|'{'
number|'8'
op|':'
string|'"byte"'
op|','
number|'16'
op|':'
string|'"short"'
op|'}'
newline|'\n'
DECL|variable|MASKS
name|'MASKS'
op|'='
op|'{'
number|'8'
op|':'
string|'" & 0xFFL"'
op|','
number|'16'
op|':'
string|'" & 0xFFFFL"'
op|','
number|'32'
op|':'
string|'" & 0xFFFFFFFFL"'
op|','
number|'64'
op|':'
string|'""'
op|'}'
newline|'\n'
DECL|variable|CASTS
name|'CASTS'
op|'='
op|'{'
number|'8'
op|':'
string|'"(byte) "'
op|','
number|'16'
op|':'
string|'"(short) "'
op|','
number|'32'
op|':'
string|'"(int) "'
op|','
number|'64'
op|':'
string|'""'
op|'}'
newline|'\n'
nl|'\n'
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
indent|'  '
name|'for'
name|'bpv'
name|'in'
name|'TYPES'
op|'.'
name|'keys'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'type'
newline|'\n'
DECL|variable|f
name|'f'
op|'='
name|'open'
op|'('
string|'"Packed%dThreeBlocks.java"'
op|'%'
name|'bpv'
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
string|'"""/**\n * Packs integers into 3 %ss (%d bits per value).\n * @lucene.internal\n */\\n"""'
op|'%'
op|'('
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|','
name|'bpv'
op|'*'
number|'3'
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"final class Packed%dThreeBlocks extends PackedInts.MutableImpl {\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  final %s[] blocks;\\n\\n"'
op|'%'
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  public static final int MAX_SIZE = Integer.MAX_VALUE / 3;\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  Packed%dThreeBlocks(int valueCount) {\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    super(valueCount, %d);\\n"'
op|'%'
op|'('
name|'bpv'
op|'*'
number|'3'
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    if (valueCount > MAX_SIZE) {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      throw new ArrayIndexOutOfBoundsException(\\"MAX_SIZE exceeded\\");\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    blocks = new %s[valueCount * 3];\\n"'
op|'%'
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  Packed%dThreeBlocks(int packedIntsVersion, DataInput in, int valueCount) throws IOException {\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    this(valueCount);\\n"'
op|')'
newline|'\n'
name|'if'
name|'bpv'
op|'=='
number|'8'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    in.readBytes(blocks, 0, 3 * valueCount);\\n"'
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
string|'"    for (int i = 0; i < 3 * valueCount; ++i) {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      blocks[i] = in.read%s();\\n"'
op|'%'
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|'.'
name|'title'
op|'('
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
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
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"""\n  @Override\n  public long get(int index) {\n    final int o = index * 3;\n    return (blocks[o]%s) << %d | (blocks[o+1]%s) << %d | (blocks[o+2]%s);\n  }\n\n  @Override\n  public int get(int index, long[] arr, int off, int len) {\n    assert len > 0 : "len must be > 0 (got " + len + ")";\n    assert index >= 0 && index < valueCount;\n    assert off + len <= arr.length;\n\n    final int gets = Math.min(valueCount - index, len);\n    for (int i = index * 3, end = (index + gets) * 3; i < end; i+=3) {\n      arr[off++] = (blocks[i]%s) << %d | (blocks[i+1]%s) << %d | (blocks[i+2]%s);\n    }\n    return gets;\n  }\n\n  @Override\n  public void set(int index, long value) {\n    final int o = index * 3;\n    blocks[o] = %s(value >>> %d);\n    blocks[o+1] = %s(value >>> %d);\n    blocks[o+2] = %svalue;\n  }\n\n  @Override\n  public int set(int index, long[] arr, int off, int len) {\n    assert len > 0 : "len must be > 0 (got " + len + ")";\n    assert index >= 0 && index < valueCount;\n    assert off + len <= arr.length;\n\n    final int sets = Math.min(valueCount - index, len);\n    for (int i = off, o = index * 3, end = off + sets; i < end; ++i) {\n      final long value = arr[i];\n      blocks[o++] = %s(value >>> %d);\n      blocks[o++] = %s(value >>> %d);\n      blocks[o++] = %svalue;\n    }\n    return sets;\n  }\n\n  @Override\n  public void fill(int fromIndex, int toIndex, long val) {\n    final %s block1 = %s(val >>> %d);\n    final %s block2 = %s(val >>> %d);\n    final %s block3 = %sval;\n    for (int i = fromIndex * 3, end = toIndex * 3; i < end; i += 3) {\n      blocks[i] = block1;\n      blocks[i+1] = block2;\n      blocks[i+2] = block3;\n    }\n  }\n\n  @Override\n  public void clear() {\n    Arrays.fill(blocks, %s0);\n  }\n\n  @Override\n  public long ramBytesUsed() {\n    return RamUsageEstimator.alignObjectSize(\n        RamUsageEstimator.NUM_BYTES_OBJECT_HEADER\n        + 2 * RamUsageEstimator.NUM_BYTES_INT     // valueCount,bitsPerValue\n        + RamUsageEstimator.NUM_BYTES_OBJECT_REF) // blocks ref\n        + RamUsageEstimator.sizeOf(blocks);\n  }\n\n  @Override\n  public String toString() {\n    return getClass().getSimpleName() + "(bitsPerValue=" + bitsPerValue\n        + ",size=" + size() + ",blocks=" + blocks.length + ")";\n  }\n}\n"""'
op|'%'
op|'('
name|'MASKS'
op|'['
name|'bpv'
op|']'
op|','
number|'2'
op|'*'
name|'bpv'
op|','
name|'MASKS'
op|'['
name|'bpv'
op|']'
op|','
name|'bpv'
op|','
name|'MASKS'
op|'['
name|'bpv'
op|']'
op|','
name|'MASKS'
op|'['
name|'bpv'
op|']'
op|','
number|'2'
op|'*'
name|'bpv'
op|','
name|'MASKS'
op|'['
name|'bpv'
op|']'
op|','
name|'bpv'
op|','
name|'MASKS'
op|'['
name|'bpv'
op|']'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
number|'2'
op|'*'
name|'bpv'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
name|'bpv'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
nl|'\n'
number|'2'
op|'*'
name|'bpv'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
name|'bpv'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
number|'2'
op|'*'
name|'bpv'
op|','
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|','
nl|'\n'
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
name|'bpv'
op|','
name|'TYPES'
op|'['
name|'bpv'
op|']'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|','
name|'CASTS'
op|'['
name|'bpv'
op|']'
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
dedent|''
dedent|''
endmarker|''
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|CodecUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|FieldInfosFormat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|FieldInfosReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|FieldInfosWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataOutput
import|;
end_import
begin_comment
comment|// javadoc
end_comment
begin_comment
comment|/**  * Lucene 4.0 Field Infos format.  *<p>  *<p>Field names are stored in the field info file, with suffix<tt>.fnm</tt>.</p>  *<p>FieldInfos (.fnm) --&gt; Header,FieldsCount,&lt;FieldName,FieldNumber,  * FieldBits,DocValuesBits,Attributes&gt;<sup>FieldsCount</sup></p>  *<p>Data types:  *<ul>  *<li>Header --&gt; {@link CodecUtil#checkHeader CodecHeader}</li>  *<li>FieldsCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>FieldName --&gt; {@link DataOutput#writeString String}</li>  *<li>FieldBits, DocValuesBits --&gt; {@link DataOutput#writeByte Byte}</li>  *<li>FieldNumber --&gt; {@link DataOutput#writeInt VInt}</li>  *<li>Attributes --&gt; {@link DataOutput#writeStringStringMap Map&lt;String,String&gt;}</li>  *</ul>  *</p>  * Field Descriptions:  *<ul>  *<li>FieldsCount: the number of fields in this file.</li>  *<li>FieldName: name of the field as a UTF-8 String.</li>  *<li>FieldNumber: the field's number. Note that unlike previous versions of  *       Lucene, the fields are not numbered implicitly by their order in the  *       file, instead explicitly.</li>  *<li>FieldBits: a byte containing field options.  *<ul>  *<li>The low-order bit is one for indexed fields, and zero for non-indexed  *             fields.</li>  *<li>The second lowest-order bit is one for fields that have term vectors  *             stored, and zero for fields without term vectors.</li>  *<li>If the third lowest order-bit is set (0x4), offsets are stored into  *             the postings list in addition to positions.</li>  *<li>Fourth bit is unused.</li>  *<li>If the fifth lowest-order bit is set (0x10), norms are omitted for the  *             indexed field.</li>  *<li>If the sixth lowest-order bit is set (0x20), payloads are stored for the  *             indexed field.</li>  *<li>If the seventh lowest-order bit is set (0x40), term frequencies and  *             positions omitted for the indexed field.</li>  *<li>If the eighth lowest-order bit is set (0x80), positions are omitted for the  *             indexed field.</li>  *</ul>  *</li>  *<li>DocValuesBits: a byte containing per-document value types. The type  *        recorded as two four-bit integers, with the high-order bits representing  *<code>norms</code> options, and the low-order bits representing   *        {@code DocValues} options. Each four-bit integer can be decoded as such:  *<ul>  *<li>0: no DocValues for this field.</li>  *<li>1: variable-width signed integers. ({@code Type#VAR_INTS VAR_INTS})</li>  *<li>2: 32-bit floating point values. ({@code Type#FLOAT_32 FLOAT_32})</li>  *<li>3: 64-bit floating point values. ({@code Type#FLOAT_64 FLOAT_64})</li>  *<li>4: fixed-length byte array values. ({@code Type#BYTES_FIXED_STRAIGHT BYTES_FIXED_STRAIGHT})</li>  *<li>5: fixed-length dereferenced byte array values. ({@code Type#BYTES_FIXED_DEREF BYTES_FIXED_DEREF})</li>  *<li>6: variable-length byte array values. ({@code Type#BYTES_VAR_STRAIGHT BYTES_VAR_STRAIGHT})</li>  *<li>7: variable-length dereferenced byte array values. ({@code Type#BYTES_VAR_DEREF BYTES_VAR_DEREF})</li>  *<li>8: 16-bit signed integers. ({@code Type#FIXED_INTS_16 FIXED_INTS_16})</li>  *<li>9: 32-bit signed integers. ({@code Type#FIXED_INTS_32 FIXED_INTS_32})</li>  *<li>10: 64-bit signed integers. ({@code Type#FIXED_INTS_64 FIXED_INTS_64})</li>  *<li>11: 8-bit signed integers. ({@code Type#FIXED_INTS_8 FIXED_INTS_8})</li>  *<li>12: fixed-length sorted byte array values. ({@code Type#BYTES_FIXED_SORTED BYTES_FIXED_SORTED})</li>  *<li>13: variable-length sorted byte array values. ({@code Type#BYTES_VAR_SORTED BYTES_VAR_SORTED})</li>  *</ul>  *</li>  *<li>Attributes: a key-value map of codec-private attributes.</li>  *</ul>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene40FieldInfosFormat
specifier|public
class|class
name|Lucene40FieldInfosFormat
extends|extends
name|FieldInfosFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|FieldInfosReader
name|reader
init|=
operator|new
name|Lucene40FieldInfosReader
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|FieldInfosWriter
name|writer
init|=
operator|new
name|Lucene40FieldInfosWriter
argument_list|()
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Lucene40FieldInfosFormat
specifier|public
name|Lucene40FieldInfosFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getFieldInfosReader
specifier|public
name|FieldInfosReader
name|getFieldInfosReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfosWriter
specifier|public
name|FieldInfosWriter
name|getFieldInfosWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|writer
return|;
block|}
block|}
end_class
end_unit

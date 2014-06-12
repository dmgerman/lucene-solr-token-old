begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|DocValuesConsumer
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
name|DocValuesProducer
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
name|DocValuesFormat
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
name|index
operator|.
name|SegmentReadState
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
name|index
operator|.
name|SegmentWriteState
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
name|index
operator|.
name|FieldInfo
operator|.
name|DocValuesType
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SmallFloat
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
name|util
operator|.
name|fst
operator|.
name|FST
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
name|util
operator|.
name|packed
operator|.
name|DirectWriter
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
name|util
operator|.
name|packed
operator|.
name|MonotonicBlockPackedWriter
import|;
end_import
begin_comment
comment|/**  * Lucene 4.9 DocValues format.  *<p>  * Encodes the five per-document value types (Numeric,Binary,Sorted,SortedSet,SortedNumeric) with these strategies:  *<p>  * {@link DocValuesType#NUMERIC NUMERIC}:  *<ul>  *<li>Delta-compressed: per-document integers written as deltas from the minimum value,  *        compressed with bitpacking. For more information, see {@link DirectWriter}.  *<li>Table-compressed: when the number of unique values is very small (&lt; 256), and  *        when there are unused "gaps" in the range of values used (such as {@link SmallFloat}),   *        a lookup table is written instead. Each per-document entry is instead the ordinal   *        to this table, and those ordinals are compressed with bitpacking ({@link DirectWriter}).   *<li>GCD-compressed: when all numbers share a common divisor, such as dates, the greatest  *        common denominator (GCD) is computed, and quotients are stored using Delta-compressed Numerics.  *<li>Monotonic-compressed: when all numbers are monotonically increasing offsets, they are written  *        as blocks of bitpacked integers, encoding the deviation from the expected delta.  *</ul>  *<p>  * {@link DocValuesType#BINARY BINARY}:  *<ul>  *<li>Fixed-width Binary: one large concatenated byte[] is written, along with the fixed length.  *        Each document's value can be addressed directly with multiplication ({@code docID * length}).   *<li>Variable-width Binary: one large concatenated byte[] is written, along with end addresses   *        for each document. The addresses are written as Monotonic-compressed numerics.  *<li>Prefix-compressed Binary: values are written in chunks of 16, with the first value written  *        completely and other values sharing prefixes. chunk addresses are written as Monotonic-compressed  *        numerics.  *</ul>  *<p>  * {@link DocValuesType#SORTED SORTED}:  *<ul>  *<li>Sorted: a mapping of ordinals to deduplicated terms is written as Prefix-Compressed Binary,   *        along with the per-document ordinals written using one of the numeric strategies above.  *</ul>  *<p>  * {@link DocValuesType#SORTED_SET SORTED_SET}:  *<ul>  *<li>SortedSet: a mapping of ordinals to deduplicated terms is written as Prefix-Compressed Binary,   *        an ordinal list and per-document index into this list are written using the numeric strategies   *        above.   *</ul>  *<p>  * {@link DocValuesType#SORTED_NUMERIC SORTED_NUMERIC}:  *<ul>  *<li>SortedNumeric: a value list and per-document index into this list are written using the numeric  *        strategies above.  *</ul>  *<p>  * Files:  *<ol>  *<li><tt>.dvd</tt>: DocValues data</li>  *<li><tt>.dvm</tt>: DocValues metadata</li>  *</ol>  *<ol>  *<li><a name="dvm" id="dvm"></a>  *<p>The DocValues metadata or .dvm file.</p>  *<p>For DocValues field, this stores metadata, such as the offset into the   *      DocValues data (.dvd)</p>  *<p>DocValues metadata (.dvm) --&gt; Header,&lt;Entry&gt;<sup>NumFields</sup>,Footer</p>  *<ul>  *<li>Entry --&gt; NumericEntry | BinaryEntry | SortedEntry | SortedSetEntry | SortedNumericEntry</li>  *<li>NumericEntry --&gt; GCDNumericEntry | TableNumericEntry | DeltaNumericEntry</li>  *<li>GCDNumericEntry --&gt; NumericHeader,MinValue,GCD,BitsPerValue</li>  *<li>TableNumericEntry --&gt; NumericHeader,TableSize,{@link DataOutput#writeLong Int64}<sup>TableSize</sup>,BitsPerValue</li>  *<li>DeltaNumericEntry --&gt; NumericHeader,MinValue,BitsPerValue</li>  *<li>MonotonicNumericEntry --&gt; NumericHeader,PackedVersion,BlockSize</li>  *<li>NumericHeader --&gt; FieldNumber,EntryType,NumericType,MissingOffset,DataOffset,Count,EndOffset</li>  *<li>BinaryEntry --&gt; FixedBinaryEntry | VariableBinaryEntry | PrefixBinaryEntry</li>  *<li>FixedBinaryEntry --&gt; BinaryHeader</li>  *<li>VariableBinaryEntry --&gt; BinaryHeader,AddressOffset,PackedVersion,BlockSize</li>  *<li>PrefixBinaryEntry --&gt; BinaryHeader,AddressInterval,AddressOffset,PackedVersion,BlockSize</li>  *<li>BinaryHeader --&gt; FieldNumber,EntryType,BinaryType,MissingOffset,MinLength,MaxLength,DataOffset</li>  *<li>SortedEntry --&gt; FieldNumber,EntryType,BinaryEntry,NumericEntry</li>  *<li>SortedSetEntry --&gt; EntryType,BinaryEntry,NumericEntry,NumericEntry</li>  *<li>SortedNumericEntry --&gt; EntryType,NumericEntry,NumericEntry</li>  *<li>FieldNumber,PackedVersion,MinLength,MaxLength,BlockSize,ValueCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>EntryType,CompressionType --&gt; {@link DataOutput#writeByte Byte}</li>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>MinValue,GCD,MissingOffset,AddressOffset,DataOffset,EndOffset --&gt; {@link DataOutput#writeLong Int64}</li>  *<li>TableSize,BitsPerValue --&gt; {@link DataOutput#writeVInt vInt}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *<p>Sorted fields have two entries: a BinaryEntry with the value metadata,  *      and an ordinary NumericEntry for the document-to-ord metadata.</p>  *<p>SortedSet fields have three entries: a BinaryEntry with the value metadata,  *      and two NumericEntries for the document-to-ord-index and ordinal list metadata.</p>  *<p>SortedNumeric fields have two entries: A NumericEntry with the value metadata,  *      and a numeric entry with the document-to-value index.</p>  *<p>FieldNumber of -1 indicates the end of metadata.</p>  *<p>EntryType is a 0 (NumericEntry) or 1 (BinaryEntry)</p>  *<p>DataOffset is the pointer to the start of the data in the DocValues data (.dvd)</p>  *<p>EndOffset is the pointer to the end of the data in the DocValues data (.dvd)</p>  *<p>NumericType indicates how Numeric values will be compressed:  *<ul>  *<li>0 --&gt; delta-compressed. For each block of 16k integers, every integer is delta-encoded  *             from the minimum value within the block.   *<li>1 --&gt, gcd-compressed. When all integers share a common divisor, only quotients are stored  *             using blocks of delta-encoded ints.  *<li>2 --&gt; table-compressed. When the number of unique numeric values is small and it would save space,  *             a lookup table of unique values is written, followed by the ordinal for each document.  *</ul>  *<p>BinaryType indicates how Binary values will be stored:  *<ul>  *<li>0 --&gt; fixed-width. All values have the same length, addressing by multiplication.   *<li>1 --&gt, variable-width. An address for each value is stored.  *<li>2 --&gt; prefix-compressed. An address to the start of every interval'th value is stored.  *</ul>  *<p>MinLength and MaxLength represent the min and max byte[] value lengths for Binary values.  *      If they are equal, then all values are of a fixed size, and can be addressed as DataOffset + (docID * length).  *      Otherwise, the binary values are of variable size, and packed integer metadata (PackedVersion,BlockSize)  *      is written for the addresses.  *<p>MissingOffset points to a byte[] containing a bitset of all documents that had a value for the field.  *      If its -1, then there are no missing values.  *<p>Checksum contains the CRC32 checksum of all bytes in the .dvm file up  *      until the checksum. This is used to verify integrity of the file on opening the  *      index.  *<li><a name="dvd" id="dvd"></a>  *<p>The DocValues data or .dvd file.</p>  *<p>For DocValues field, this stores the actual per-document data (the heavy-lifting)</p>  *<p>DocValues data (.dvd) --&gt; Header,&lt;NumericData | BinaryData | SortedData&gt;<sup>NumFields</sup>,Footer</p>  *<ul>  *<li>NumericData --&gt; DeltaCompressedNumerics | TableCompressedNumerics | GCDCompressedNumerics</li>  *<li>BinaryData --&gt;  {@link DataOutput#writeByte Byte}<sup>DataLength</sup>,Addresses</li>  *<li>SortedData --&gt; {@link FST FST&lt;Int64&gt;}</li>  *<li>DeltaCompressedNumerics,TableCompressedNumerics,GCDCompressedNumerics --&gt; {@link DirectWriter PackedInts}</li>  *<li>Addresses --&gt; {@link MonotonicBlockPackedWriter MonotonicBlockPackedInts(blockSize=16k)}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *</ol>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene49DocValuesFormat
specifier|public
specifier|final
class|class
name|Lucene49DocValuesFormat
extends|extends
name|DocValuesFormat
block|{
comment|/** Sole Constructor */
DECL|method|Lucene49DocValuesFormat
specifier|public
name|Lucene49DocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene49"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|DocValuesConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene49DocValuesConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|META_CODEC
argument_list|,
name|META_EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|DocValuesProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene49DocValuesProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|META_CODEC
argument_list|,
name|META_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene49DocValuesData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dvd"
decl_stmt|;
DECL|field|META_CODEC
specifier|static
specifier|final
name|String
name|META_CODEC
init|=
literal|"Lucene49ValuesMetadata"
decl_stmt|;
DECL|field|META_EXTENSION
specifier|static
specifier|final
name|String
name|META_EXTENSION
init|=
literal|"dvm"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|NUMERIC
specifier|static
specifier|final
name|byte
name|NUMERIC
init|=
literal|0
decl_stmt|;
DECL|field|BINARY
specifier|static
specifier|final
name|byte
name|BINARY
init|=
literal|1
decl_stmt|;
DECL|field|SORTED
specifier|static
specifier|final
name|byte
name|SORTED
init|=
literal|2
decl_stmt|;
DECL|field|SORTED_SET
specifier|static
specifier|final
name|byte
name|SORTED_SET
init|=
literal|3
decl_stmt|;
DECL|field|SORTED_NUMERIC
specifier|static
specifier|final
name|byte
name|SORTED_NUMERIC
init|=
literal|4
decl_stmt|;
block|}
end_class
end_unit

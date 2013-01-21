begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|PostingsFormat
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
name|codecs
operator|.
name|asserting
operator|.
name|AssertingPostingsFormat
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
name|lucene41
operator|.
name|Lucene41Codec
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
name|lucene41
operator|.
name|Lucene41DocValuesFormat
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
name|lucene41
operator|.
name|Lucene41PostingsFormat
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
name|lucene41ords
operator|.
name|Lucene41WithOrds
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
name|bloom
operator|.
name|TestBloomFilteredLucene41Postings
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
name|diskdv
operator|.
name|DiskDocValuesFormat
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
name|memory
operator|.
name|DirectPostingsFormat
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
name|memory
operator|.
name|MemoryPostingsFormat
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
name|mockintblock
operator|.
name|MockFixedIntBlockPostingsFormat
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
name|mockintblock
operator|.
name|MockVariableIntBlockPostingsFormat
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
name|mockrandom
operator|.
name|MockRandomPostingsFormat
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
name|mocksep
operator|.
name|MockSepPostingsFormat
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
name|nestedpulsing
operator|.
name|NestedPulsingPostingsFormat
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
name|pulsing
operator|.
name|Pulsing41PostingsFormat
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
name|simpletext
operator|.
name|SimpleTextPostingsFormat
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
name|simpletext
operator|.
name|SimpleTextDocValuesFormat
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
name|LuceneTestCase
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Codec that assigns per-field random postings formats.  *<p>  * The same field/format assignment will happen regardless of order,  * a hash is computed up front that determines the mapping.  * This means fields can be put into things like HashSets and added to  * documents in different orders and the test will still be deterministic  * and reproducable.  */
end_comment
begin_class
DECL|class|RandomCodec
specifier|public
class|class
name|RandomCodec
extends|extends
name|Lucene41Codec
block|{
comment|/** Shuffled list of postings formats to use for new mappings */
DECL|field|formats
specifier|private
name|List
argument_list|<
name|PostingsFormat
argument_list|>
name|formats
init|=
operator|new
name|ArrayList
argument_list|<
name|PostingsFormat
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Shuffled list of docvalues formats to use for new mappings */
DECL|field|dvFormats
specifier|private
name|List
argument_list|<
name|DocValuesFormat
argument_list|>
name|dvFormats
init|=
operator|new
name|ArrayList
argument_list|<
name|DocValuesFormat
argument_list|>
argument_list|()
decl_stmt|;
comment|/** unique set of format names this codec knows about */
DECL|field|formatNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|formatNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/** unique set of docvalues format names this codec knows about */
DECL|field|dvFormatNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|dvFormatNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/** memorized field->postingsformat mappings */
comment|// note: we have to sync this map even though its just for debugging/toString,
comment|// otherwise DWPT's .toString() calls that iterate over the map can
comment|// cause concurrentmodificationexception if indexwriter's infostream is on
DECL|field|previousMappings
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PostingsFormat
argument_list|>
name|previousMappings
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PostingsFormat
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|previousDVMappings
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesFormat
argument_list|>
name|previousDVMappings
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesFormat
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|perFieldSeed
specifier|private
specifier|final
name|int
name|perFieldSeed
decl_stmt|;
annotation|@
name|Override
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PostingsFormat
name|codec
init|=
name|previousMappings
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
name|codec
operator|=
name|formats
operator|.
name|get
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|perFieldSeed
operator|^
name|name
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|%
name|formats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|codec
operator|instanceof
name|SimpleTextPostingsFormat
operator|&&
name|perFieldSeed
operator|%
literal|5
operator|!=
literal|0
condition|)
block|{
comment|// make simpletext rarer, choose again
name|codec
operator|=
name|formats
operator|.
name|get
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|perFieldSeed
operator|^
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|%
name|formats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|previousMappings
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|codec
argument_list|)
expr_stmt|;
comment|// Safety:
assert|assert
name|previousMappings
operator|.
name|size
argument_list|()
operator|<
literal|10000
operator|:
literal|"test went insane"
assert|;
block|}
return|return
name|codec
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesFormatForField
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|DocValuesFormat
name|codec
init|=
name|previousDVMappings
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
name|codec
operator|=
name|dvFormats
operator|.
name|get
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|perFieldSeed
operator|^
name|name
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|%
name|dvFormats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|codec
operator|instanceof
name|SimpleTextDocValuesFormat
operator|&&
name|perFieldSeed
operator|%
literal|5
operator|!=
literal|0
condition|)
block|{
comment|// make simpletext rarer, choose again
name|codec
operator|=
name|dvFormats
operator|.
name|get
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|perFieldSeed
operator|^
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|%
name|dvFormats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|previousDVMappings
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|codec
argument_list|)
expr_stmt|;
comment|// Safety:
assert|assert
name|previousDVMappings
operator|.
name|size
argument_list|()
operator|<
literal|10000
operator|:
literal|"test went insane"
assert|;
block|}
return|return
name|codec
return|;
block|}
DECL|method|RandomCodec
specifier|public
name|RandomCodec
parameter_list|(
name|Random
name|random
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|avoidCodecs
parameter_list|)
block|{
name|this
operator|.
name|perFieldSeed
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
comment|// TODO: make it possible to specify min/max iterms per
comment|// block via CL:
name|int
name|minItemsPerBlock
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|maxItemsPerBlock
init|=
literal|2
operator|*
operator|(
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
name|minItemsPerBlock
operator|-
literal|1
argument_list|)
operator|)
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|int
name|lowFreqCutoff
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|avoidCodecs
argument_list|,
operator|new
name|Lucene41PostingsFormat
argument_list|(
name|minItemsPerBlock
argument_list|,
name|maxItemsPerBlock
argument_list|)
argument_list|,
operator|new
name|DirectPostingsFormat
argument_list|(
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|random
argument_list|)
condition|?
literal|1
else|:
operator|(
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|random
argument_list|)
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|maxItemsPerBlock
operator|)
argument_list|,
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|random
argument_list|)
condition|?
literal|1
else|:
operator|(
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|random
argument_list|)
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|lowFreqCutoff
operator|)
argument_list|)
argument_list|,
operator|new
name|Pulsing41PostingsFormat
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|,
name|minItemsPerBlock
argument_list|,
name|maxItemsPerBlock
argument_list|)
argument_list|,
comment|// add pulsing again with (usually) different parameters
operator|new
name|Pulsing41PostingsFormat
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|,
name|minItemsPerBlock
argument_list|,
name|maxItemsPerBlock
argument_list|)
argument_list|,
comment|//TODO as a PostingsFormat which wraps others, we should allow TestBloomFilteredLucene41Postings to be constructed
comment|//with a choice of concrete PostingsFormats. Maybe useful to have a generic means of marking and dealing
comment|//with such "wrapper" classes?
operator|new
name|TestBloomFilteredLucene41Postings
argument_list|()
argument_list|,
operator|new
name|MockSepPostingsFormat
argument_list|()
argument_list|,
operator|new
name|MockFixedIntBlockPostingsFormat
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|2000
argument_list|)
argument_list|)
argument_list|,
operator|new
name|MockVariableIntBlockPostingsFormat
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|127
argument_list|)
argument_list|)
argument_list|,
operator|new
name|MockRandomPostingsFormat
argument_list|(
name|random
argument_list|)
argument_list|,
operator|new
name|NestedPulsingPostingsFormat
argument_list|()
argument_list|,
operator|new
name|Lucene41WithOrds
argument_list|()
argument_list|,
operator|new
name|SimpleTextPostingsFormat
argument_list|()
argument_list|,
operator|new
name|AssertingPostingsFormat
argument_list|()
argument_list|,
operator|new
name|MemoryPostingsFormat
argument_list|(
literal|true
argument_list|,
name|random
operator|.
name|nextFloat
argument_list|()
argument_list|)
argument_list|,
operator|new
name|MemoryPostingsFormat
argument_list|(
literal|false
argument_list|,
name|random
operator|.
name|nextFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addDocValues
argument_list|(
name|avoidCodecs
argument_list|,
operator|new
name|Lucene41DocValuesFormat
argument_list|()
argument_list|,
operator|new
name|DiskDocValuesFormat
argument_list|()
argument_list|,
operator|new
name|SimpleTextDocValuesFormat
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|formats
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|dvFormats
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// Avoid too many open files:
name|formats
operator|=
name|formats
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|// only if we get big dvFormats = dvFormats.subList(0, 4);
block|}
DECL|method|RandomCodec
specifier|public
name|RandomCodec
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|private
specifier|final
name|void
name|add
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|avoidCodecs
parameter_list|,
name|PostingsFormat
modifier|...
name|postings
parameter_list|)
block|{
for|for
control|(
name|PostingsFormat
name|p
range|:
name|postings
control|)
block|{
if|if
condition|(
operator|!
name|avoidCodecs
operator|.
name|contains
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|formats
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|formatNames
operator|.
name|add
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addDocValues
specifier|private
specifier|final
name|void
name|addDocValues
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|avoidCodecs
parameter_list|,
name|DocValuesFormat
modifier|...
name|docvalues
parameter_list|)
block|{
for|for
control|(
name|DocValuesFormat
name|d
range|:
name|docvalues
control|)
block|{
if|if
condition|(
operator|!
name|avoidCodecs
operator|.
name|contains
argument_list|(
name|d
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dvFormats
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|dvFormatNames
operator|.
name|add
argument_list|(
name|d
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|previousMappings
operator|.
name|toString
argument_list|()
operator|+
literal|", docValues:"
operator|+
name|previousDVMappings
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit

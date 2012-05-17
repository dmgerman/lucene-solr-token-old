begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.mockrandom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|mockrandom
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|BlockTermsReader
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
name|BlockTermsWriter
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
name|BlockTreeTermsReader
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
name|BlockTreeTermsWriter
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|FixedGapTermsIndexReader
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
name|FixedGapTermsIndexWriter
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
name|PostingsReaderBase
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
name|PostingsWriterBase
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
name|TermStats
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
name|TermsIndexReaderBase
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
name|TermsIndexWriterBase
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
name|VariableGapTermsIndexReader
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
name|VariableGapTermsIndexWriter
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
name|lucene40
operator|.
name|Lucene40PostingsReader
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
name|lucene40
operator|.
name|Lucene40PostingsWriter
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
name|mocksep
operator|.
name|MockSingleIntFactory
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
name|PulsingPostingsReader
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
name|PulsingPostingsWriter
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
name|sep
operator|.
name|IntIndexInput
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
name|sep
operator|.
name|IntIndexOutput
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
name|sep
operator|.
name|IntStreamFactory
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
name|sep
operator|.
name|SepPostingsReader
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
name|sep
operator|.
name|SepPostingsWriter
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
name|IndexFileNames
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
name|SegmentInfo
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
name|store
operator|.
name|Directory
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
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|BytesRef
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
comment|/**  * Randomly combines terms index impl w/ postings impls.  */
end_comment
begin_class
DECL|class|MockRandomPostingsFormat
specifier|public
class|class
name|MockRandomPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|seedRandom
specifier|private
specifier|final
name|Random
name|seedRandom
decl_stmt|;
DECL|field|SEED_EXT
specifier|private
specifier|final
name|String
name|SEED_EXT
init|=
literal|"sd"
decl_stmt|;
DECL|method|MockRandomPostingsFormat
specifier|public
name|MockRandomPostingsFormat
parameter_list|()
block|{
comment|// just for reading, we are gonna setSeed from the .seed file... right?
name|this
argument_list|(
operator|new
name|Random
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRandomPostingsFormat
specifier|public
name|MockRandomPostingsFormat
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
literal|"MockRandom"
argument_list|)
expr_stmt|;
name|this
operator|.
name|seedRandom
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Chooses random IntStreamFactory depending on file's extension
DECL|class|MockIntStreamFactory
specifier|private
specifier|static
class|class
name|MockIntStreamFactory
extends|extends
name|IntStreamFactory
block|{
DECL|field|salt
specifier|private
specifier|final
name|int
name|salt
decl_stmt|;
DECL|field|delegates
specifier|private
specifier|final
name|List
argument_list|<
name|IntStreamFactory
argument_list|>
name|delegates
init|=
operator|new
name|ArrayList
argument_list|<
name|IntStreamFactory
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MockIntStreamFactory
specifier|public
name|MockIntStreamFactory
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|salt
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|delegates
operator|.
name|add
argument_list|(
operator|new
name|MockSingleIntFactory
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|blockSize
init|=
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
decl_stmt|;
name|delegates
operator|.
name|add
argument_list|(
operator|new
name|MockFixedIntBlockPostingsFormat
operator|.
name|MockIntFactory
argument_list|(
name|blockSize
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|baseBlockSize
init|=
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
decl_stmt|;
name|delegates
operator|.
name|add
argument_list|(
operator|new
name|MockVariableIntBlockPostingsFormat
operator|.
name|MockIntFactory
argument_list|(
name|baseBlockSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: others
block|}
DECL|method|getExtension
specifier|private
specifier|static
name|String
name|getExtension
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|fileName
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
assert|assert
name|idx
operator|!=
operator|-
literal|1
assert|;
return|return
name|fileName
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IntIndexInput
name|openInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Must only use extension, because IW.addIndexes can
comment|// rename segment!
specifier|final
name|IntStreamFactory
name|f
init|=
name|delegates
operator|.
name|get
argument_list|(
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|salt
operator|^
name|getExtension
argument_list|(
name|fileName
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|)
operator|%
name|delegates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: read using int factory "
operator|+
name|f
operator|+
literal|" from fileName="
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
return|return
name|f
operator|.
name|openInput
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IntIndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IntStreamFactory
name|f
init|=
name|delegates
operator|.
name|get
argument_list|(
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|salt
operator|^
name|getExtension
argument_list|(
name|fileName
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|)
operator|%
name|delegates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: write using int factory "
operator|+
name|f
operator|+
literal|" to fileName="
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
return|return
name|f
operator|.
name|createOutput
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we pull this before the seed intentionally: because its not consumed at runtime
comment|// (the skipInterval is written into postings header)
name|int
name|skipInterval
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|seedRandom
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: skipInterval="
operator|+
name|skipInterval
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|seed
init|=
name|seedRandom
operator|.
name|nextLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing to seg="
operator|+
name|state
operator|.
name|segmentName
operator|+
literal|" formatID="
operator|+
name|state
operator|.
name|segmentSuffix
operator|+
literal|" seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|seedFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|SEED_EXT
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|out
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|seedFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
comment|// consume a random for buffersize
name|PostingsWriterBase
name|postingsWriter
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|postingsWriter
operator|=
operator|new
name|SepPostingsWriter
argument_list|(
name|state
argument_list|,
operator|new
name|MockIntStreamFactory
argument_list|(
name|random
argument_list|)
argument_list|,
name|skipInterval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing Standard postings"
argument_list|)
expr_stmt|;
block|}
name|postingsWriter
operator|=
operator|new
name|Lucene40PostingsWriter
argument_list|(
name|state
argument_list|,
name|skipInterval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|totTFCutoff
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing pulsing postings with totTFCutoff="
operator|+
name|totTFCutoff
argument_list|)
expr_stmt|;
block|}
name|postingsWriter
operator|=
operator|new
name|PulsingPostingsWriter
argument_list|(
name|totTFCutoff
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FieldsConsumer
name|fields
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Use BlockTree terms dict
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing BlockTree terms dict"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: would be nice to allow 1 but this is very
comment|// slow to write
specifier|final
name|int
name|minTermsInBlock
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
specifier|final
name|int
name|maxTermsInBlock
init|=
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
operator|(
name|minTermsInBlock
operator|-
literal|1
operator|)
operator|*
literal|2
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|postingsWriter
argument_list|,
name|minTermsInBlock
argument_list|,
name|maxTermsInBlock
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing Block terms dict"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|TermsIndexWriterBase
name|indexWriter
decl_stmt|;
try|try
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|state
operator|.
name|termIndexInterval
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: fixed-gap terms index (tii="
operator|+
name|state
operator|.
name|termIndexInterval
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|=
operator|new
name|FixedGapTermsIndexWriter
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|VariableGapTermsIndexWriter
operator|.
name|IndexTermSelector
name|selector
decl_stmt|;
specifier|final
name|int
name|n2
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|n2
operator|==
literal|0
condition|)
block|{
specifier|final
name|int
name|tii
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|selector
operator|=
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|EveryNTermSelector
argument_list|(
name|tii
argument_list|)
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: variable-gap terms index (tii="
operator|+
name|tii
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|n2
operator|==
literal|1
condition|)
block|{
specifier|final
name|int
name|docFreqThresh
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
specifier|final
name|int
name|tii
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|selector
operator|=
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|EveryNOrDocFreqTermSelector
argument_list|(
name|docFreqThresh
argument_list|,
name|tii
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|seed2
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|gap
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|40
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: random-gap terms index (max gap="
operator|+
name|gap
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|selector
operator|=
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|IndexTermSelector
argument_list|()
block|{
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed2
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isIndexTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermStats
name|stats
parameter_list|)
block|{
return|return
name|rand
operator|.
name|nextInt
argument_list|(
name|gap
argument_list|)
operator|==
name|gap
operator|/
literal|2
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|newField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{                 }
block|}
expr_stmt|;
block|}
name|indexWriter
operator|=
operator|new
name|VariableGapTermsIndexWriter
argument_list|(
name|state
argument_list|,
name|selector
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTermsWriter
argument_list|(
name|indexWriter
argument_list|,
name|state
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|seedFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|SEED_EXT
argument_list|)
decl_stmt|;
specifier|final
name|IndexInput
name|in
init|=
name|state
operator|.
name|dir
operator|.
name|openInput
argument_list|(
name|seedFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading from seg="
operator|+
name|state
operator|.
name|segmentInfo
operator|.
name|name
operator|+
literal|" formatID="
operator|+
name|state
operator|.
name|segmentSuffix
operator|+
literal|" seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|int
name|readBufferSize
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: readBufferSize="
operator|+
name|readBufferSize
argument_list|)
expr_stmt|;
block|}
name|PostingsReaderBase
name|postingsReader
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading Sep postings"
argument_list|)
expr_stmt|;
block|}
name|postingsReader
operator|=
operator|new
name|SepPostingsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|,
operator|new
name|MockIntStreamFactory
argument_list|(
name|random
argument_list|)
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading Standard postings"
argument_list|)
expr_stmt|;
block|}
name|postingsReader
operator|=
operator|new
name|Lucene40PostingsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|totTFCutoff
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading pulsing postings with totTFCutoff="
operator|+
name|totTFCutoff
argument_list|)
expr_stmt|;
block|}
name|postingsReader
operator|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|postingsReader
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FieldsProducer
name|fields
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Use BlockTree terms dict
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading BlockTree terms dict"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTreeTermsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|postingsReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading Block terms dict"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|TermsIndexReaderBase
name|indexReader
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|boolean
name|doFixedGap
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// randomness diverges from writer, here:
if|if
condition|(
name|state
operator|.
name|termsIndexDivisor
operator|!=
operator|-
literal|1
condition|)
block|{
name|state
operator|.
name|termsIndexDivisor
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doFixedGap
condition|)
block|{
comment|// if termsIndexDivisor is set to -1, we should not touch it. It means a
comment|// test explicitly instructed not to load the terms index.
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: fixed-gap terms index (divisor="
operator|+
name|state
operator|.
name|termsIndexDivisor
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|=
operator|new
name|FixedGapTermsIndexReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|n2
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|n2
operator|==
literal|1
condition|)
block|{
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n2
operator|==
literal|2
condition|)
block|{
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: variable-gap terms index (divisor="
operator|+
name|state
operator|.
name|termsIndexDivisor
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|=
operator|new
name|VariableGapTermsIndexReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|termsCacheSize
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTermsReader
argument_list|(
name|indexReader
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|postingsReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|termsCacheSize
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|seedFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|SEED_EXT
argument_list|)
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|seedFileName
argument_list|)
expr_stmt|;
name|SepPostingsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|Lucene40PostingsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|BlockTermsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|BlockTreeTermsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|FixedGapTermsIndexReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|VariableGapTermsIndexReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// hackish!
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|files
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|String
name|file
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|segmentInfo
operator|.
name|dir
operator|.
name|fileExists
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|//System.out.println("MockRandom.files return " + files);
block|}
block|}
end_class
end_unit

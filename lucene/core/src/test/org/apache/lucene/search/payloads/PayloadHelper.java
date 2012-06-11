begin_unit
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|*
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|DirectoryReader
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
name|IndexWriterConfig
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
name|IndexWriter
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|TextField
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
name|English
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|MockDirectoryWrapper
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
name|RAMDirectory
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
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
name|Random
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|PayloadHelper
specifier|public
class|class
name|PayloadHelper
block|{
DECL|field|payloadField
specifier|private
name|byte
index|[]
name|payloadField
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|}
decl_stmt|;
DECL|field|payloadMultiField1
specifier|private
name|byte
index|[]
name|payloadMultiField1
init|=
operator|new
name|byte
index|[]
block|{
literal|2
block|}
decl_stmt|;
DECL|field|payloadMultiField2
specifier|private
name|byte
index|[]
name|payloadMultiField2
init|=
operator|new
name|byte
index|[]
block|{
literal|4
block|}
decl_stmt|;
DECL|field|NO_PAYLOAD_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|NO_PAYLOAD_FIELD
init|=
literal|"noPayloadField"
decl_stmt|;
DECL|field|MULTI_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|MULTI_FIELD
init|=
literal|"multiField"
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|reader
specifier|public
name|IndexReader
name|reader
decl_stmt|;
DECL|class|PayloadAnalyzer
specifier|public
specifier|final
class|class
name|PayloadAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|PayloadAnalyzer
specifier|public
name|PayloadAnalyzer
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|PerFieldReuseStrategy
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|result
argument_list|,
operator|new
name|PayloadFilter
argument_list|(
name|result
argument_list|,
name|fieldName
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|PayloadFilter
specifier|public
specifier|final
class|class
name|PayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|numSeen
specifier|private
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
DECL|field|payloadAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|method|PayloadFilter
specifier|public
name|PayloadFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|FIELD
argument_list|)
condition|)
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payloadField
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|MULTI_FIELD
argument_list|)
condition|)
block|{
if|if
condition|(
name|numSeen
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payloadMultiField1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payloadMultiField2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numSeen
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|numSeen
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * Sets up a RAMDirectory, and adds documents (using English.intToEnglish()) with two fields: field and multiField    * and analyzes them using the PayloadAnalyzer    * @param similarity The Similarity class to use in the Searcher    * @param numDocs The num docs to add    * @return An IndexSearcher    * @throws IOException    */
comment|// TODO: randomize
DECL|method|setUp
specifier|public
name|IndexSearcher
name|setUp
parameter_list|(
name|Random
name|random
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|PayloadAnalyzer
name|analyzer
init|=
operator|new
name|PayloadAnalyzer
argument_list|()
decl_stmt|;
comment|// TODO randomize this
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
argument_list|)
decl_stmt|;
comment|// writer.infoStream = System.out;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|FIELD
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|MULTI_FIELD
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|"  "
operator|+
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|NO_PAYLOAD_FIELD
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|LuceneTestCase
operator|.
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
return|return
name|searcher
return|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

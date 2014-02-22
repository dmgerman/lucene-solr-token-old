begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|ExternalPaths
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  * Test for UpdateRequestCodec  *  * @since solr 1.4  *  * @see org.apache.solr.client.solrj.request.UpdateRequest  */
end_comment
begin_class
DECL|class|TestUpdateRequestCodec
specifier|public
class|class
name|TestUpdateRequestCodec
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|simple
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|updateRequest
operator|.
name|deleteById
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteById
argument_list|(
literal|"id:5"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"2*"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"1*"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|setParam
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"one"
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setDocumentBoost
argument_list|(
literal|10.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"three"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|foobar
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|foobar
operator|.
name|add
argument_list|(
literal|"baz1"
argument_list|)
expr_stmt|;
name|foobar
operator|.
name|add
argument_list|(
literal|"baz2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"foobar"
argument_list|,
name|foobar
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//    updateRequest.setWaitFlush(true);
name|updateRequest
operator|.
name|deleteById
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"id:3"
argument_list|)
expr_stmt|;
name|JavaBinUpdateRequestCodec
name|codec
init|=
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|codec
operator|.
name|marshal
argument_list|(
name|updateRequest
argument_list|,
name|baos
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
decl_stmt|;
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
name|handler
init|=
operator|new
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|UpdateRequest
name|req
parameter_list|,
name|Integer
name|commitWithin
parameter_list|,
name|Boolean
name|overwrite
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|UpdateRequest
name|updateUnmarshalled
init|=
name|codec
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|handler
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrInputDocument
name|document
range|:
name|docs
control|)
block|{
name|updateUnmarshalled
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|inDoc
init|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|outDoc
init|=
name|updateUnmarshalled
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|compareDocs
argument_list|(
literal|"doc#"
operator|+
name|i
argument_list|,
name|inDoc
argument_list|,
name|outDoc
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|updateUnmarshalled
operator|.
name|getDeleteById
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|updateRequest
operator|.
name|getDeleteById
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|updateUnmarshalled
operator|.
name|getDeleteQuery
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|updateRequest
operator|.
name|getDeleteQuery
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|updateUnmarshalled
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIteratable
specifier|public
name|void
name|testIteratable
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
literal|"iterItem1"
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
literal|"iterItem2"
argument_list|)
expr_stmt|;
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"one"
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
comment|// imagine someone adding a custom Bean that implements Iterable
comment|// but is not a Collection
name|doc
operator|.
name|addField
argument_list|(
literal|"iter"
argument_list|,
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|values
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|JavaBinUpdateRequestCodec
name|codec
init|=
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|codec
operator|.
name|marshal
argument_list|(
name|updateRequest
argument_list|,
name|baos
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
decl_stmt|;
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
name|handler
init|=
operator|new
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|UpdateRequest
name|req
parameter_list|,
name|Integer
name|commitWithin
parameter_list|,
name|Boolean
name|overwrite
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|UpdateRequest
name|updateUnmarshalled
init|=
name|codec
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|handler
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrInputDocument
name|document
range|:
name|docs
control|)
block|{
name|updateUnmarshalled
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|SolrInputDocument
name|outDoc
init|=
name|updateUnmarshalled
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SolrInputField
name|iter
init|=
name|outDoc
operator|.
name|getField
argument_list|(
literal|"iter"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"iter field is null"
argument_list|,
name|iter
argument_list|)
expr_stmt|;
name|Object
name|iterVal
init|=
name|iter
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"iterVal is not a Collection"
argument_list|,
name|iterVal
operator|instanceof
name|Collection
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"iterVal contents"
argument_list|,
name|values
argument_list|,
name|iterVal
argument_list|)
expr_stmt|;
block|}
DECL|method|testBackCompat4_5
specifier|public
name|void
name|testBackCompat4_5
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|updateRequest
operator|.
name|deleteById
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteById
argument_list|(
literal|"id:5"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"2*"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"1*"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|setParam
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"one"
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setDocumentBoost
argument_list|(
literal|10.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"three"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"desc"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|foobar
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|foobar
operator|.
name|add
argument_list|(
literal|"baz1"
argument_list|)
expr_stmt|;
name|foobar
operator|.
name|add
argument_list|(
literal|"baz2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"foobar"
argument_list|,
name|foobar
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteById
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|deleteByQuery
argument_list|(
literal|"id:3"
argument_list|)
expr_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SOURCE_HOME
argument_list|,
literal|"solrj/src/test-files/solrj/updateReq_4_5.bin"
argument_list|)
argument_list|)
decl_stmt|;
name|UpdateRequest
name|updateUnmarshalled
init|=
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|is
argument_list|,
operator|new
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|UpdateRequest
name|req
parameter_list|,
name|Integer
name|commitWithin
parameter_list|,
name|Boolean
name|override
parameter_list|)
block|{
if|if
condition|(
name|commitWithin
operator|==
literal|null
condition|)
block|{
name|req
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Doc"
operator|+
name|document
operator|+
literal|" ,commitWithin:"
operator|+
name|commitWithin
operator|+
literal|" , override:"
operator|+
name|override
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|updateUnmarshalled
operator|.
name|getDocumentsMap
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|updateUnmarshalled
operator|.
name|getDocuments
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|inDoc
init|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|outDoc
init|=
name|updateUnmarshalled
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|compareDocs
argument_list|(
literal|"doc#"
operator|+
name|i
argument_list|,
name|inDoc
argument_list|,
name|outDoc
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|updateUnmarshalled
operator|.
name|getDeleteById
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|updateRequest
operator|.
name|getDeleteById
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|updateUnmarshalled
operator|.
name|getDeleteQuery
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|updateRequest
operator|.
name|getDeleteQuery
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|updateUnmarshalled
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|compareDocs
specifier|private
name|void
name|compareDocs
parameter_list|(
name|String
name|m
parameter_list|,
name|SolrInputDocument
name|expectedDoc
parameter_list|,
name|SolrInputDocument
name|actualDoc
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedDoc
operator|.
name|getDocumentBoost
argument_list|()
argument_list|,
name|actualDoc
operator|.
name|getDocumentBoost
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|expectedDoc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|SolrInputField
name|expectedField
init|=
name|expectedDoc
operator|.
name|getField
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|SolrInputField
name|actualField
init|=
name|actualDoc
operator|.
name|getField
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m
operator|+
literal|": diff boosts for field: "
operator|+
name|s
argument_list|,
name|expectedField
operator|.
name|getBoost
argument_list|()
argument_list|,
name|actualField
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|expectedVal
init|=
name|expectedField
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|actualVal
init|=
name|actualField
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectedVal
operator|instanceof
name|Set
operator|&&
name|actualVal
operator|instanceof
name|Collection
condition|)
block|{
comment|// unmarshaled documents never contain Sets, they are just a
comment|// List in an arbitrary order based on what the iterator of
comment|// hte original Set returned, so we need a comparison that is
comment|// order agnostic.
name|actualVal
operator|=
operator|new
name|HashSet
argument_list|(
operator|(
name|Collection
operator|)
name|actualVal
argument_list|)
expr_stmt|;
name|m
operator|+=
literal|" (Set comparison)"
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m
operator|+
literal|" diff values for field: "
operator|+
name|s
argument_list|,
name|expectedVal
argument_list|,
name|actualVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|index
operator|.
name|Term
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
name|Query
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
name|TermQuery
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
name|Searcher
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
name|Hits
import|;
end_import
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Tests {@link Document} class.  *  * @author Otis Gospodnetic  * @version $Id$  */
end_comment
begin_class
DECL|class|TestDocument
specifier|public
class|class
name|TestDocument
extends|extends
name|TestCase
block|{
comment|/**    * Tests {@link Document#removeField(String)} method for a brand new Document    * that has not been indexed yet.    *    * @throws Exception on error    */
DECL|method|testRemoveForNewDocument
specifier|public
name|void
name|testRemoveForNewDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|makeDocumentWithFields
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"keyword"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"doesnotexists"
argument_list|)
expr_stmt|;
comment|// removing non-existing fields is siltenlty ignored
name|doc
operator|.
name|removeFields
argument_list|(
literal|"keyword"
argument_list|)
expr_stmt|;
comment|// removing a field more than once
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"doesnotexists"
argument_list|)
expr_stmt|;
comment|// removing non-existing fields is siltenlty ignored
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"unindexed"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"unstored"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"doesnotexists"
argument_list|)
expr_stmt|;
comment|// removing non-existing fields is siltenlty ignored
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstructorExceptions
specifier|public
name|void
name|testConstructorExceptions
parameter_list|()
block|{
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
expr_stmt|;
comment|// okay
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
expr_stmt|;
comment|// okay
try|try
block|{
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
comment|// okay
try|try
block|{
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
comment|/**      * Tests {@link Document#getValues(String)} method for a brand new Document      * that has not been indexed yet.      *      * @throws Exception on error      */
DECL|method|testGetValuesForNewDocument
specifier|public
name|void
name|testGetValuesForNewDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|doAssert
argument_list|(
name|makeDocumentWithFields
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests {@link Document#getValues(String)} method for a Document retrieved from      * an index.      *      * @throws Exception on error      */
DECL|method|testGetValuesForIndexedDocument
specifier|public
name|void
name|testGetValuesForIndexedDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|makeDocumentWithFields
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// search for something that does exists
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ensure that queries return expected results without DateFilter first
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|doAssert
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|makeDocumentWithFields
specifier|private
name|Document
name|makeDocumentWithFields
parameter_list|()
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
name|Field
operator|.
name|Keyword
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"keyword"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
literal|"test1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
literal|"test2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
literal|"unstored"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
literal|"unstored"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|doAssert
specifier|private
name|void
name|doAssert
parameter_list|(
name|Document
name|doc
parameter_list|,
name|boolean
name|fromIndex
parameter_list|)
block|{
name|String
index|[]
name|keywordFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"keyword"
argument_list|)
decl_stmt|;
name|String
index|[]
name|textFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|String
index|[]
name|unindexedFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"unindexed"
argument_list|)
decl_stmt|;
name|String
index|[]
name|unstoredFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"unstored"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|keywordFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// this test cannot work for documents retrieved from the index
comment|// since unstored fields will obviously not be returned
if|if
condition|(
operator|!
name|fromIndex
condition|)
block|{
name|assertTrue
argument_list|(
name|unstoredFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|keywordFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keywordFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// this test cannot work for documents retrieved from the index
comment|// since unstored fields will obviously not be returned
if|if
condition|(
operator|!
name|fromIndex
condition|)
block|{
name|assertTrue
argument_list|(
name|unstoredFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unstoredFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

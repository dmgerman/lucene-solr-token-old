begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
package|;
end_package
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
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|Schema
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
name|avro
operator|.
name|file
operator|.
name|DataFileReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|file
operator|.
name|FileReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|generic
operator|.
name|GenericData
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|generic
operator|.
name|GenericDatumReader
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
name|Constants
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
operator|.
name|Slow
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
operator|.
name|SuppressCodecs
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|SolrDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakAction
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakAction
operator|.
name|Action
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakLingering
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakZombies
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakZombies
operator|.
name|Consequence
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Fields
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import
begin_class
annotation|@
name|ThreadLeakAction
argument_list|(
block|{
name|Action
operator|.
name|WARN
block|}
argument_list|)
annotation|@
name|ThreadLeakLingering
argument_list|(
name|linger
operator|=
literal|0
argument_list|)
annotation|@
name|ThreadLeakZombies
argument_list|(
name|Consequence
operator|.
name|CONTINUE
argument_list|)
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|}
argument_list|)
annotation|@
name|Slow
DECL|class|SolrMorphlineZkAvroTest
specifier|public
class|class
name|SolrMorphlineZkAvroTest
extends|extends
name|AbstractSolrMorphlineZkTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass2
specifier|public
specifier|static
name|void
name|beforeClass2
parameter_list|()
block|{
name|assumeFalse
argument_list|(
literal|"FIXME: This test fails under Java 8 due to the Saxon dependency - see SOLR-1301"
argument_list|,
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA8
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"FIXME: This test fails under J9 due to the Saxon dependency - see SOLR-1301"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.info"
argument_list|,
literal|"<?>"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM J9"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/test-documents/sample-statuses-20120906-141433-medium.avro"
argument_list|)
decl_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// load avro records via morphline and zk into solr
name|morphline
operator|=
name|parse
argument_list|(
literal|"test-morphlines/tutorialReadAvroContainer"
argument_list|)
expr_stmt|;
name|Record
name|record
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
name|byte
index|[]
name|body
init|=
name|Files
operator|.
name|toByteArray
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_BODY
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|startSession
argument_list|()
expr_stmt|;
name|Notifications
operator|.
name|notifyBeginTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|morphline
operator|.
name|process
argument_list|(
name|record
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collector
operator|.
name|getNumStartEvents
argument_list|()
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// fetch sorted result set from solr
name|QueryResponse
name|rsp
init|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|100000
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"id"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2104
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|collector
operator|.
name|getRecords
argument_list|()
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Record
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Record
name|r1
parameter_list|,
name|Record
name|r2
parameter_list|)
block|{
return|return
name|r1
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|r2
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// fetch test input data and sort like solr result set
name|List
argument_list|<
name|GenericData
operator|.
name|Record
argument_list|>
name|records
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|FileReader
argument_list|<
name|GenericData
operator|.
name|Record
argument_list|>
name|reader
init|=
operator|new
name|DataFileReader
argument_list|(
name|file
argument_list|,
operator|new
name|GenericDatumReader
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|GenericData
operator|.
name|Record
name|expected
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|records
operator|.
name|add
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|records
argument_list|,
operator|new
name|Comparator
argument_list|<
name|GenericData
operator|.
name|Record
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|GenericData
operator|.
name|Record
name|r1
parameter_list|,
name|GenericData
operator|.
name|Record
name|r2
parameter_list|)
block|{
return|return
name|r1
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|r2
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Object
name|lastId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|records
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//System.out.println("myrec" + i + ":" + records.get(i));
name|Object
name|id
init|=
name|records
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|equals
argument_list|(
name|lastId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Detected duplicate id. Test input data must not contain duplicate ids!"
argument_list|)
throw|;
block|}
name|lastId
operator|=
name|id
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
name|records
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//System.out.println("myrsp" + i + ":" + rsp.getResults().get(i));
block|}
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|rspIter
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|records
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// verify morphline spat out expected data
name|Record
name|actual
init|=
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|GenericData
operator|.
name|Record
name|expected
init|=
name|records
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertTweetEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// verify Solr result set contains expected data
name|actual
operator|=
operator|new
name|Record
argument_list|()
expr_stmt|;
name|actual
operator|.
name|getFields
argument_list|()
operator|.
name|putAll
argument_list|(
name|next
argument_list|(
name|rspIter
argument_list|)
argument_list|)
expr_stmt|;
name|assertTweetEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|Notifications
operator|.
name|notifyRollbackTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyShutdown
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|assertTweetEquals
specifier|private
name|void
name|assertTweetEquals
parameter_list|(
name|GenericData
operator|.
name|Record
name|expected
parameter_list|,
name|Record
name|actual
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|actual
argument_list|)
expr_stmt|;
comment|//    System.out.println("\n\nexpected: " + toString(expected));
comment|//    System.out.println("actual:   " + actual);
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"in_reply_to_status_id"
block|,
literal|"in_reply_to_user_id"
block|,
literal|"retweet_count"
block|,
literal|"text"
block|,          }
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|assertEquals
argument_list|(
name|i
operator|+
literal|" fieldName: "
operator|+
name|fieldName
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|actual
operator|.
name|getFirstValue
argument_list|(
name|fieldName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|private
name|String
name|toString
parameter_list|(
name|GenericData
operator|.
name|Record
name|avroRecord
parameter_list|)
block|{
name|Record
name|record
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|avroRecord
operator|.
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
name|record
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|avroRecord
operator|.
name|get
argument_list|(
name|field
operator|.
name|pos
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|record
operator|.
name|toString
argument_list|()
return|;
comment|// prints sorted by key for human readability
block|}
block|}
end_class
end_unit
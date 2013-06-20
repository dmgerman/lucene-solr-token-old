begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|index
operator|.
name|AtomicReader
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
name|AtomicReaderContext
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
name|SegmentReader
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
name|MergePolicy
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
name|TieredMergePolicy
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
name|core
operator|.
name|SolrCore
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
name|RefCounted
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
name|search
operator|.
name|SolrIndexSearcher
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
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_class
DECL|class|TestMergePolicyConfig
specifier|public
class|class
name|TestMergePolicyConfig
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|docIdCounter
specifier|private
specifier|static
name|AtomicInteger
name|docIdCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|42
argument_list|)
decl_stmt|;
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaultMergePolicyConfig
specifier|public
name|void
name|testDefaultMergePolicyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-mergepolicy-defaults.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|solrConfig
operator|.
name|indexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|iwc
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
name|TieredMergePolicy
name|tieredMP
init|=
name|assertAndCast
argument_list|(
name|TieredMergePolicy
operator|.
name|class
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.0D
argument_list|,
name|tieredMP
operator|.
name|getNoCFSRatio
argument_list|()
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertCommitSomeNewDocs
argument_list|()
expr_stmt|;
name|assertCompoundSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testLegacyMergePolicyConfig
specifier|public
name|void
name|testLegacyMergePolicyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-mergepolicy-legacy.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|solrConfig
operator|.
name|indexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|iwc
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
name|TieredMergePolicy
name|tieredMP
init|=
name|assertAndCast
argument_list|(
name|TieredMergePolicy
operator|.
name|class
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|tieredMP
operator|.
name|getMaxMergeAtOnce
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7.0D
argument_list|,
name|tieredMP
operator|.
name|getSegmentsPerTier
argument_list|()
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0D
argument_list|,
name|tieredMP
operator|.
name|getNoCFSRatio
argument_list|()
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertCommitSomeNewDocs
argument_list|()
expr_stmt|;
name|assertCompoundSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testTieredMergePolicyConfig
specifier|public
name|void
name|testTieredMergePolicyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-mergepolicy.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|solrConfig
operator|.
name|indexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|iwc
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
name|TieredMergePolicy
name|tieredMP
init|=
name|assertAndCast
argument_list|(
name|TieredMergePolicy
operator|.
name|class
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
comment|// set by legacy<mergeFactor> setting
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|tieredMP
operator|.
name|getMaxMergeAtOnce
argument_list|()
argument_list|)
expr_stmt|;
comment|// mp-specific setters
name|assertEquals
argument_list|(
literal|19
argument_list|,
name|tieredMP
operator|.
name|getMaxMergeAtOnceExplicit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1D
argument_list|,
name|tieredMP
operator|.
name|getNoCFSRatio
argument_list|()
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
comment|// make sure we overrode segmentsPerTier
comment|// (split from maxMergeAtOnce out of mergeFactor)
name|assertEquals
argument_list|(
literal|9D
argument_list|,
name|tieredMP
operator|.
name|getSegmentsPerTier
argument_list|()
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertCommitSomeNewDocs
argument_list|()
expr_stmt|;
comment|// even though we have a single segment (which is 100% of the size of
comment|// the index which is higher then our 0.6D threashold) the
comment|// compound ratio doesn't matter because the segment was never merged
name|assertCompoundSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertCommitSomeNewDocs
argument_list|()
expr_stmt|;
name|assertNumSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertCompoundSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertNumSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// we've now forced a merge, and the MP ratio should be in play
name|assertCompoundSegments
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given a Type and an object asserts that the object is non-null and an     * instance of the specified Type.  The object is then cast to that type and     * returned.    */
DECL|method|assertAndCast
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|assertAndCast
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|method|assertCommitSomeNewDocs
specifier|public
specifier|static
name|void
name|assertCommitSomeNewDocs
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|int
name|val
init|=
name|docIdCounter
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|val
argument_list|,
literal|"a_s"
argument_list|,
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
argument_list|,
literal|"b_s"
argument_list|,
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
argument_list|,
literal|"c_s"
argument_list|,
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
argument_list|,
literal|"d_s"
argument_list|,
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
argument_list|,
literal|"e_s"
argument_list|,
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
argument_list|,
literal|"f_s"
argument_list|,
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
operator|+
literal|"_"
operator|+
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given an SolrCore, asserts that the number of leave segments in     * the index reader matches the expected value.    */
DECL|method|assertNumSegments
specifier|public
specifier|static
name|void
name|assertNumSegments
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherRef
init|=
name|core
operator|.
name|getRegisteredSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|searcherRef
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcherRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Given an SolrCore, asserts that each segment in the (searchable) index     * has a compound file status that matches the expected input.    */
DECL|method|assertCompoundSegments
specifier|public
specifier|static
name|void
name|assertCompoundSegments
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|compound
parameter_list|)
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherRef
init|=
name|core
operator|.
name|getRegisteredSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|assertCompoundSegments
argument_list|(
name|searcherRef
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|compound
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcherRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Given an IndexReader, asserts that there is at least one AtomcReader leaf,    * and that all AtomicReader leaves are SegmentReader's that have a compound     * file status that matches the expected input.    */
DECL|method|assertCompoundSegments
specifier|private
specifier|static
name|void
name|assertCompoundSegments
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|compound
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Null leaves"
argument_list|,
name|reader
operator|.
name|leaves
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no leaves"
argument_list|,
literal|0
operator|<
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AtomicReaderContext
name|atomic
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"not a segment reader: "
operator|+
name|atomic
operator|.
name|reader
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|atomic
operator|.
name|reader
argument_list|()
operator|instanceof
name|SegmentReader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Compound status incorrect for: "
operator|+
name|atomic
operator|.
name|reader
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|compound
argument_list|,
operator|(
operator|(
name|SegmentReader
operator|)
name|atomic
operator|.
name|reader
argument_list|()
operator|)
operator|.
name|getSegmentInfo
argument_list|()
operator|.
name|info
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

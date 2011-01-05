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
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|LogByteSizeMergePolicy
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
name|update
operator|.
name|DirectUpdateHandler2
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
name|AbstractSolrTestCase
import|;
end_import
begin_class
DECL|class|TestPropInject
specifier|public
class|class
name|TestPropInject
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-propinject.xml"
return|;
block|}
DECL|class|ExposeWriterHandler
class|class
name|ExposeWriterHandler
extends|extends
name|DirectUpdateHandler2
block|{
DECL|method|ExposeWriterHandler
specifier|public
name|ExposeWriterHandler
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getWriter
specifier|public
name|IndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|forceOpenWriter
argument_list|()
expr_stmt|;
return|return
name|writer
return|;
block|}
block|}
DECL|method|testMergePolicy
specifier|public
name|void
name|testMergePolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|ExposeWriterHandler
name|uh
init|=
operator|new
name|ExposeWriterHandler
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
name|uh
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|LogByteSizeMergePolicy
name|mp
init|=
operator|(
name|LogByteSizeMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|64.0
argument_list|,
name|mp
operator|.
name|getMaxMergeMB
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|uh
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testProps
specifier|public
name|void
name|testProps
parameter_list|()
throws|throws
name|Exception
block|{
name|ExposeWriterHandler
name|uh
init|=
operator|new
name|ExposeWriterHandler
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
name|uh
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|ConcurrentMergeScheduler
name|cms
init|=
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cms
operator|.
name|getMaxThreadCount
argument_list|()
argument_list|)
expr_stmt|;
name|uh
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

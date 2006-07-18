begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
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
name|TestHarness
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_comment
comment|/**  * New WordDelimiterFilter tests... most of the tests are in ConvertedLegacyTest  */
end_comment
begin_class
DECL|class|TestWordDelimiterFilter
specifier|public
class|class
name|TestWordDelimiterFilter
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
literal|"solr/conf/schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solr/conf/solrconfig.xml"
return|;
block|}
DECL|method|posTst
specifier|public
name|void
name|posTst
parameter_list|(
name|String
name|v1
parameter_list|,
name|String
name|v2
parameter_list|,
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"subword"
argument_list|,
name|v1
argument_list|,
literal|"subword"
argument_list|,
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// there is a positionIncrementGap of 100 between field values, so
comment|// we test if that was maintained.
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~90"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~110"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRetainPositionIncrement
specifier|public
name|void
name|testRetainPositionIncrement
parameter_list|()
block|{
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"123"
argument_list|,
literal|"456"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/"
argument_list|,
literal|"/456/"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/abc"
argument_list|,
literal|"qwe/456/"
argument_list|,
literal|"abc"
argument_list|,
literal|"qwe"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo"
argument_list|,
literal|"bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo-123"
argument_list|,
literal|"456-bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoGenerationEdgeCase
specifier|public
name|void
name|testNoGenerationEdgeCase
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"222"
argument_list|,
literal|"numberpartfail"
argument_list|,
literal|"123.123.123.123"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|SimpleOrderedMap
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
name|Config
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
name|DocList
import|;
end_import
begin_class
DECL|class|DummyHighlighter
specifier|public
class|class
name|DummyHighlighter
extends|extends
name|SolrHighlighter
block|{
annotation|@
name|Override
DECL|method|doHighlighting
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|doHighlighting
parameter_list|(
name|DocList
name|docs
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
name|fragments
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|fragments
operator|.
name|add
argument_list|(
literal|"dummy"
argument_list|,
literal|"thing1"
argument_list|)
expr_stmt|;
return|return
name|fragments
return|;
block|}
annotation|@
name|Override
DECL|method|initalize
specifier|public
name|void
name|initalize
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
end_class
end_unit

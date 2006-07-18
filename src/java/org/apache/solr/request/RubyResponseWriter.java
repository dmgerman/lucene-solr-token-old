begin_unit
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|Writer
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
begin_class
DECL|class|RubyResponseWriter
specifier|public
class|class
name|RubyResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|field|CONTENT_TYPE_RUBY_UTF8
specifier|static
name|String
name|CONTENT_TYPE_RUBY_UTF8
init|=
literal|"text/x-ruby;charset=UTF-8"
decl_stmt|;
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|RubyWriter
name|w
init|=
operator|new
name|RubyWriter
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
name|w
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|CONTENT_TYPE_TEXT_UTF8
return|;
block|}
block|}
end_class
end_unit

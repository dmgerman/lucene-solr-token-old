begin_unit
begin_package
DECL|package|com.netwebapps.taglib.search
package|package
name|com
operator|.
name|netwebapps
operator|.
name|taglib
operator|.
name|search
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|jsp
operator|.
name|*
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|jsp
operator|.
name|tagext
operator|.
name|*
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
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
name|document
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
name|index
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
name|search
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
name|queryParser
operator|.
name|*
import|;
end_import
begin_comment
comment|/*  *   * @author Bryan LaPlante  * @param   *  */
end_comment
begin_class
DECL|class|SearchTag
specifier|public
class|class
name|SearchTag
extends|extends
name|BodyTagSupport
block|{
DECL|field|hitMap
specifier|private
name|HashMap
name|hitMap
init|=
literal|null
decl_stmt|;
DECL|field|hitArray
specifier|private
name|ArrayList
name|hitArray
init|=
literal|null
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
init|=
literal|""
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
init|=
literal|null
decl_stmt|;
DECL|field|hits
specifier|private
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
DECL|field|thispage
specifier|private
name|int
name|thispage
init|=
literal|0
decl_stmt|;
DECL|field|criteria
specifier|private
name|String
name|criteria
init|=
literal|""
decl_stmt|;
DECL|field|searchItr
specifier|private
name|Iterator
name|searchItr
init|=
literal|null
decl_stmt|;
DECL|field|fields
specifier|private
name|Enumeration
name|fields
init|=
literal|null
decl_stmt|;
DECL|field|aField
specifier|private
name|HashMap
name|aField
init|=
literal|null
decl_stmt|;
DECL|field|ROWCOUNT
specifier|private
name|int
name|ROWCOUNT
init|=
literal|0
decl_stmt|;
DECL|field|PAGECOUNT
specifier|private
name|int
name|PAGECOUNT
init|=
literal|1
decl_stmt|;
DECL|field|HITCOUNT
specifier|private
name|int
name|HITCOUNT
init|=
literal|0
decl_stmt|;
DECL|field|startRow
specifier|public
name|int
name|startRow
init|=
literal|0
decl_stmt|;
DECL|field|maxRows
specifier|public
name|int
name|maxRows
init|=
literal|50
decl_stmt|;
DECL|field|rowCount
specifier|public
name|String
name|rowCount
init|=
literal|""
decl_stmt|;
DECL|field|pageCount
specifier|public
name|String
name|pageCount
init|=
literal|"1"
decl_stmt|;
DECL|field|hitCount
specifier|public
name|String
name|hitCount
init|=
literal|""
decl_stmt|;
DECL|field|firstPage
specifier|public
name|String
name|firstPage
init|=
literal|""
decl_stmt|;
DECL|field|nextPage
specifier|public
name|String
name|nextPage
init|=
literal|""
decl_stmt|;
DECL|field|previousPage
specifier|public
name|String
name|previousPage
init|=
literal|""
decl_stmt|;
DECL|field|lastPage
specifier|public
name|String
name|lastPage
init|=
literal|""
decl_stmt|;
DECL|field|pageList
specifier|public
name|LinkedList
name|pageList
init|=
literal|null
decl_stmt|;
DECL|method|doStartTag
specifier|public
name|int
name|doStartTag
parameter_list|()
throws|throws
name|JspException
block|{
name|doSearch
argument_list|()
expr_stmt|;
name|searchItr
operator|=
name|hitArray
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|aField
operator|=
operator|(
name|HashMap
operator|)
name|searchItr
operator|.
name|next
argument_list|()
expr_stmt|;
name|rowCount
operator|=
operator|new
name|Integer
argument_list|(
name|startRow
operator|+
name|ROWCOUNT
operator|++
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|pageContext
operator|.
name|setAttribute
argument_list|(
name|getId
argument_list|()
argument_list|,
name|this
argument_list|,
name|PageContext
operator|.
name|PAGE_SCOPE
argument_list|)
expr_stmt|;
return|return
name|EVAL_BODY_AGAIN
return|;
block|}
return|return
name|SKIP_BODY
return|;
block|}
DECL|method|doInitBody
specifier|public
name|void
name|doInitBody
parameter_list|()
throws|throws
name|JspException
block|{
name|doSearch
argument_list|()
expr_stmt|;
name|searchItr
operator|=
name|hitArray
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|aField
operator|=
operator|(
name|HashMap
operator|)
name|searchItr
operator|.
name|next
argument_list|()
expr_stmt|;
name|rowCount
operator|=
operator|new
name|Integer
argument_list|(
name|startRow
operator|+
name|ROWCOUNT
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|pageContext
operator|.
name|setAttribute
argument_list|(
name|getId
argument_list|()
argument_list|,
name|this
argument_list|,
name|PageContext
operator|.
name|PAGE_SCOPE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doAfterBody
specifier|public
name|int
name|doAfterBody
parameter_list|()
throws|throws
name|JspException
block|{
try|try
block|{
name|getBodyContent
argument_list|()
operator|.
name|writeOut
argument_list|(
name|getPreviousOut
argument_list|()
argument_list|)
expr_stmt|;
name|getBodyContent
argument_list|()
operator|.
name|clearBody
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|searchItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|aField
operator|=
operator|(
name|HashMap
operator|)
name|searchItr
operator|.
name|next
argument_list|()
expr_stmt|;
name|rowCount
operator|=
operator|new
name|Integer
argument_list|(
name|startRow
operator|+
name|ROWCOUNT
operator|++
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|pageContext
operator|.
name|setAttribute
argument_list|(
name|getId
argument_list|()
argument_list|,
name|this
argument_list|,
name|PageContext
operator|.
name|PAGE_SCOPE
argument_list|)
expr_stmt|;
return|return
name|EVAL_BODY_AGAIN
return|;
block|}
return|return
name|SKIP_BODY
return|;
block|}
DECL|method|doEndTag
specifier|public
name|int
name|doEndTag
parameter_list|()
throws|throws
name|JspException
block|{
try|try
block|{
name|HttpServletRequest
name|req
init|=
operator|(
name|HttpServletRequest
operator|)
name|pageContext
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|String
name|relativePath
init|=
name|req
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|firstPage
operator|=
name|relativePath
operator|+
literal|"?startRow=0&maxRows="
operator|+
name|maxRows
expr_stmt|;
name|nextPage
operator|=
name|relativePath
operator|+
literal|"?startRow="
operator|+
operator|(
operator|(
name|startRow
operator|+
name|maxRows
operator|<=
name|HITCOUNT
operator|)
condition|?
name|startRow
operator|+
name|maxRows
else|:
name|startRow
operator|)
operator|+
literal|"&maxRows="
operator|+
name|maxRows
expr_stmt|;
name|previousPage
operator|=
name|relativePath
operator|+
literal|"?startRow="
operator|+
operator|(
operator|(
name|startRow
operator|-
name|maxRows
operator|>=
literal|0
operator|)
condition|?
name|startRow
operator|-
name|maxRows
else|:
literal|0
operator|)
operator|+
literal|"&maxRows="
operator|+
name|maxRows
expr_stmt|;
name|lastPage
operator|=
name|relativePath
operator|+
literal|"?startRow="
operator|+
operator|(
operator|(
operator|(
name|HITCOUNT
operator|-
name|maxRows
operator|)
operator|>=
literal|0
operator|)
condition|?
name|HITCOUNT
operator|-
name|maxRows
else|:
literal|0
operator|)
operator|+
literal|"&maxRows="
operator|+
name|maxRows
expr_stmt|;
if|if
condition|(
name|HITCOUNT
operator|>
literal|0
condition|)
block|{
name|pageList
operator|=
operator|new
name|LinkedList
argument_list|()
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
operator|(
name|HITCOUNT
operator|/
name|maxRows
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|String
name|tempURL
init|=
name|relativePath
operator|+
literal|"?startRow="
operator|+
operator|(
name|maxRows
operator|*
name|i
operator|)
operator|+
literal|"&maxRows="
operator|+
name|maxRows
decl_stmt|;
name|pageList
operator|.
name|add
argument_list|(
name|tempURL
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
argument_list|(
literal|"A problem occured durring doEndTag: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|pageContext
operator|.
name|setAttribute
argument_list|(
name|getId
argument_list|()
argument_list|,
name|this
argument_list|,
name|PageContext
operator|.
name|PAGE_SCOPE
argument_list|)
expr_stmt|;
return|return
name|EVAL_PAGE
return|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{ 	}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|aField
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|aField
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|aField
operator|.
name|get
argument_list|(
operator|(
name|String
operator|)
name|name
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
DECL|method|getFields
specifier|public
name|Set
name|getFields
parameter_list|()
block|{
if|if
condition|(
name|aField
operator|!=
literal|null
condition|)
block|{
return|return
name|aField
operator|.
name|keySet
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|doSearch
specifier|public
name|void
name|doSearch
parameter_list|()
throws|throws
name|JspException
block|{
try|try
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StopAnalyzer
argument_list|()
decl_stmt|;
try|try
block|{
name|query
operator|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|criteria
argument_list|,
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|hitCount
operator|=
operator|new
name|Integer
argument_list|(
name|hits
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|HITCOUNT
operator|=
name|hits
operator|.
name|length
argument_list|()
expr_stmt|;
name|PAGECOUNT
operator|=
name|PAGECOUNT
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|double
operator|)
name|startRow
operator|)
operator|/
name|maxRows
argument_list|)
expr_stmt|;
name|pageCount
operator|=
operator|new
name|Integer
argument_list|(
name|PAGECOUNT
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|thispage
operator|=
name|maxRows
expr_stmt|;
if|if
condition|(
operator|(
name|startRow
operator|+
name|maxRows
operator|)
operator|>
name|hits
operator|.
name|length
argument_list|()
condition|)
block|{
name|thispage
operator|=
name|hits
operator|.
name|length
argument_list|()
operator|-
name|startRow
expr_stmt|;
block|}
name|hitArray
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startRow
init|;
name|i
operator|<
operator|(
name|thispage
operator|+
name|startRow
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|hitMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|hitMap
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
operator|new
name|Float
argument_list|(
name|hits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|fields
argument_list|()
expr_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
name|hitMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hitArray
operator|.
name|add
argument_list|(
name|hitMap
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
argument_list|(
literal|"An error occurred while parsing the index : "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
argument_list|(
literal|"An error occurred while trying to open the search index: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* setters */
DECL|method|setCriteria
specifier|public
name|void
name|setCriteria
parameter_list|(
name|String
name|criteria
parameter_list|)
block|{
name|this
operator|.
name|criteria
operator|=
name|criteria
expr_stmt|;
block|}
DECL|method|setStartRow
specifier|public
name|void
name|setStartRow
parameter_list|(
name|String
name|startRow
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|startRow
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|startRow
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|this
operator|.
name|startRow
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|setStartRow
specifier|public
name|void
name|setStartRow
parameter_list|(
name|int
name|startRow
parameter_list|)
block|{
name|this
operator|.
name|startRow
operator|=
name|startRow
expr_stmt|;
block|}
DECL|method|setMaxRows
specifier|public
name|void
name|setMaxRows
parameter_list|(
name|String
name|maxRows
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|maxRows
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|maxRows
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|this
operator|.
name|maxRows
operator|=
literal|10
expr_stmt|;
block|}
block|}
DECL|method|setMaxRows
specifier|public
name|void
name|setMaxRows
parameter_list|(
name|int
name|maxRows
parameter_list|)
block|{
name|this
operator|.
name|maxRows
operator|=
name|maxRows
expr_stmt|;
block|}
DECL|method|setCollection
specifier|public
name|void
name|setCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
comment|/* getters */
DECL|method|getStartRow
specifier|public
name|int
name|getStartRow
parameter_list|()
block|{
return|return
name|startRow
return|;
block|}
DECL|method|getMaxRows
specifier|public
name|int
name|getMaxRows
parameter_list|()
block|{
return|return
name|maxRows
return|;
block|}
block|}
end_class
end_unit

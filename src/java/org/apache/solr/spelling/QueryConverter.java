begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package
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
name|Analyzer
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
name|Token
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
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
begin_comment
comment|/**  *<p>  * The QueryConverter is an abstract base class defining a method for converting  * input "raw" queries into a set of tokens for spell checking. It is used to  * "parse" the CommonParams.Q (the input query) and converts it to tokens.  *</p>  *   *<p>  * It is only invoked for the CommonParams.Q parameter, and<b>not</b> the  * "spellcheck.q" parameter. Systems that use their own query parser or those  * that find issue with the basic implementation will want to implement their  * own QueryConverter instead of using the provided implementation  * (SpellingQueryConverter) by overriding the appropriate methods on the  * SpellingQueryConverter and registering it in the solrconfig.xml  *</p>  *   *<p>  * Refer to http://wiki.apache.org/solr/SpellCheckComponent for more details  *</p>  *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|QueryConverter
specifier|public
specifier|abstract
class|class
name|QueryConverter
implements|implements
name|NamedListInitializedPlugin
block|{
DECL|field|args
specifier|private
name|NamedList
name|args
decl_stmt|;
DECL|field|analyzer
specifier|protected
name|Analyzer
name|analyzer
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
comment|/**    * @param original    * @return The Collection of {@link org.apache.lucene.analysis.Token}s for    *         the query. Offsets on the Token should correspond to the correct    *         offset in the origQuery    */
DECL|method|convert
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|Token
argument_list|>
name|convert
parameter_list|(
name|String
name|original
parameter_list|)
function_decl|;
comment|/**    * Set the analyzer to use. Must be set before any calls to convert.    *     * @param analyzer    */
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
block|}
end_class
end_unit

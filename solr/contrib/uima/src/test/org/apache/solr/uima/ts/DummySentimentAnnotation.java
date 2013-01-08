begin_unit
begin_comment
comment|/* First created by JCasGen Fri Mar 04 13:08:40 CET 2011 */
end_comment
begin_package
DECL|package|org.apache.solr.uima.ts
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|ts
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|JCas
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|JCasRegistry
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|cas
operator|.
name|TOP_Type
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|tcas
operator|.
name|Annotation
import|;
end_import
begin_comment
comment|/**   * Updated by JCasGen Fri Mar 04 13:08:40 CET 2011  * XML source: /Users/tommasoteofili/Documents/workspaces/lucene_workspace/lucene_dev/solr/contrib/uima/src/test/resources/DummySentimentAnalysisAEDescriptor.xml  * @generated */
end_comment
begin_class
DECL|class|DummySentimentAnnotation
specifier|public
class|class
name|DummySentimentAnnotation
extends|extends
name|Annotation
block|{
comment|/** @generated    * @ordered     */
DECL|field|typeIndexID
specifier|public
specifier|final
specifier|static
name|int
name|typeIndexID
init|=
name|JCasRegistry
operator|.
name|register
argument_list|(
name|DummySentimentAnnotation
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** @generated    * @ordered     */
DECL|field|type
specifier|public
specifier|final
specifier|static
name|int
name|type
init|=
name|typeIndexID
decl_stmt|;
comment|/** @generated  */
annotation|@
name|Override
DECL|method|getTypeIndexID
specifier|public
name|int
name|getTypeIndexID
parameter_list|()
block|{
return|return
name|typeIndexID
return|;
block|}
comment|/** Never called.  Disable default constructor    * @generated */
DECL|method|DummySentimentAnnotation
specifier|protected
name|DummySentimentAnnotation
parameter_list|()
block|{}
comment|/** Internal - constructor used by generator     * @generated */
DECL|method|DummySentimentAnnotation
specifier|public
name|DummySentimentAnnotation
parameter_list|(
name|int
name|addr
parameter_list|,
name|TOP_Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|addr
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|readObject
argument_list|()
expr_stmt|;
block|}
comment|/** @generated */
DECL|method|DummySentimentAnnotation
specifier|public
name|DummySentimentAnnotation
parameter_list|(
name|JCas
name|jcas
parameter_list|)
block|{
name|super
argument_list|(
name|jcas
argument_list|)
expr_stmt|;
name|readObject
argument_list|()
expr_stmt|;
block|}
comment|/** @generated */
DECL|method|DummySentimentAnnotation
specifier|public
name|DummySentimentAnnotation
parameter_list|(
name|JCas
name|jcas
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|super
argument_list|(
name|jcas
argument_list|)
expr_stmt|;
name|setBegin
argument_list|(
name|begin
argument_list|)
expr_stmt|;
name|setEnd
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|readObject
argument_list|()
expr_stmt|;
block|}
comment|/**<!-- begin-user-doc -->     * Write your own initialization here     *<!-- end-user-doc -->   @generated modifiable */
DECL|method|readObject
specifier|private
name|void
name|readObject
parameter_list|()
block|{}
comment|//*--------------*
comment|//* Feature: mood
comment|/** getter for mood - gets     * @generated */
DECL|method|getMood
specifier|public
name|String
name|getMood
parameter_list|()
block|{
if|if
condition|(
name|DummySentimentAnnotation_Type
operator|.
name|featOkTst
operator|&&
operator|(
operator|(
name|DummySentimentAnnotation_Type
operator|)
name|jcasType
operator|)
operator|.
name|casFeat_mood
operator|==
literal|null
condition|)
name|jcasType
operator|.
name|jcas
operator|.
name|throwFeatMissing
argument_list|(
literal|"mood"
argument_list|,
literal|"org.apache.solr.uima.ts.SentimentAnnotation"
argument_list|)
expr_stmt|;
return|return
name|jcasType
operator|.
name|ll_cas
operator|.
name|ll_getStringValue
argument_list|(
name|addr
argument_list|,
operator|(
operator|(
name|DummySentimentAnnotation_Type
operator|)
name|jcasType
operator|)
operator|.
name|casFeatCode_mood
argument_list|)
return|;
block|}
comment|/** setter for mood - sets      * @generated */
DECL|method|setMood
specifier|public
name|void
name|setMood
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|DummySentimentAnnotation_Type
operator|.
name|featOkTst
operator|&&
operator|(
operator|(
name|DummySentimentAnnotation_Type
operator|)
name|jcasType
operator|)
operator|.
name|casFeat_mood
operator|==
literal|null
condition|)
name|jcasType
operator|.
name|jcas
operator|.
name|throwFeatMissing
argument_list|(
literal|"mood"
argument_list|,
literal|"org.apache.solr.uima.ts.SentimentAnnotation"
argument_list|)
expr_stmt|;
name|jcasType
operator|.
name|ll_cas
operator|.
name|ll_setStringValue
argument_list|(
name|addr
argument_list|,
operator|(
operator|(
name|DummySentimentAnnotation_Type
operator|)
name|jcasType
operator|)
operator|.
name|casFeatCode_mood
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

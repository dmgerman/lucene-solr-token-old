begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import
begin_class
DECL|class|StoreClassNameRule
specifier|public
class|class
name|StoreClassNameRule
implements|implements
name|TestRule
block|{
DECL|field|description
specifier|private
specifier|volatile
name|Description
name|description
decl_stmt|;
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|s
parameter_list|,
specifier|final
name|Description
name|d
parameter_list|)
block|{
if|if
condition|(
operator|!
name|d
operator|.
name|isSuite
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This is a @ClassRule (applies to suites only)."
argument_list|)
throw|;
block|}
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|description
operator|=
name|d
expr_stmt|;
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|description
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Returns the test class currently executing in this rule.    */
DECL|method|getTestClass
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getTestClass
parameter_list|()
block|{
name|Description
name|localDescription
init|=
name|description
decl_stmt|;
if|if
condition|(
name|localDescription
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The rule is not currently executing."
argument_list|)
throw|;
block|}
return|return
name|localDescription
operator|.
name|getTestClass
argument_list|()
return|;
block|}
block|}
end_class
end_unit


package org.eclipse.birt.report.designer.ui.cubebuilder.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.designer.internal.ui.util.ExceptionHandler;
import org.eclipse.birt.report.designer.internal.ui.util.IHelpContextIds;
import org.eclipse.birt.report.designer.internal.ui.util.UIUtil;
import org.eclipse.birt.report.designer.internal.ui.util.WidgetUtil;
import org.eclipse.birt.report.designer.ui.cubebuilder.nls.Messages;
import org.eclipse.birt.report.designer.ui.views.attributes.providers.ChoiceSetFactory;
import org.eclipse.birt.report.designer.util.DEUtil;
/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/


import org.eclipse.birt.report.model.api.GroupHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.ReportDesignConstants;
import org.eclipse.birt.report.model.api.metadata.IChoice;
import org.eclipse.birt.report.model.api.olap.TabularLevelHandle;
import org.eclipse.birt.report.model.elements.GroupElement;
import org.eclipse.birt.report.model.elements.interfaces.IHierarchyModel;
import org.eclipse.birt.report.model.elements.interfaces.ILevelModel;
import org.eclipse.birt.report.model.metadata.MetaDataDictionary;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DateLevelDialog extends TitleAreaDialog
{

	private Text nameText;
	private Combo typeCombo;
	private Text intervalRange;
	private Button intervalBaseButton;
	private Text intervalBaseText;
	private TabularLevelHandle input;

	public DateLevelDialog( )
	{
		super( UIUtil.getDefaultShell( ) );
	}

	public void setInput( TabularLevelHandle level )
	{
		this.input = level;
	}

	private IChoice[] DATE_TIME_LEVEL_TYPE_ALL = MetaDataDictionary.getInstance( )
			.getElement( ReportDesignConstants.TABULAR_LEVEL_ELEMENT )
			.getProperty( DesignChoiceConstants.CHOICE_DATE_TIME_LEVEL_TYPE )
			.getAllowedChoices( )
			.getChoices( );
	private Button noneIntervalButton;
	private Button intervalButton;

	private List getDateTypeNames( )
	{
		IChoice[] choices = DATE_TIME_LEVEL_TYPE_ALL;
		List dateTypeList = new ArrayList( );
		if ( choices == null )
			return dateTypeList;
		for ( int i = 0; i < choices.length; i++ )
		{
			dateTypeList.add( choices[i].getName( ) );
		}
		return dateTypeList;
	}

	private String[] getAvailableDateTypeDisplayNames( )
	{
		List dateTypeList = getAvailableDateTypeNames( );
		List dateTypeDisplayList = new ArrayList( );
		for ( int i = 0; i < dateTypeList.size( ); i++ )
		{
			dateTypeDisplayList.add( getDateTypeDisplayName( dateTypeList.get( i )
					.toString( ) ) );
		}
		return (String[]) dateTypeDisplayList.toArray( new String[0] );
	}

	private List getAvailableDateTypeNames( )
	{
		List dateTypeList = new ArrayList( );
		dateTypeList.addAll( getDateTypeNames( ) );
		List levels = input.getContainer( )
				.getContents( IHierarchyModel.LEVELS_PROP );
		for ( int i = 0; i < levels.size( ); i++ )
		{
			if ( levels.get( i ) == input )
				continue;
			dateTypeList.remove( ( (TabularLevelHandle) levels.get( i ) ).getDateTimeLevelType( ) );
		}
		return dateTypeList;
	}

	public String getDateTypeDisplayName( String name )
	{
		return ChoiceSetFactory.getDisplayNameFromChoiceSet( name,
				DEUtil.getMetaDataDictionary( )
						.getElement( ReportDesignConstants.LEVEL_ELEMENT )
						.getProperty( ILevelModel.DATA_TYPE_PROP )
						.getAllowedChoices( ) );
	}

	protected Control createDialogArea( Composite parent )
	{
		UIUtil.bindHelp( parent, IHelpContextIds.PREFIX + "DateLevel_ID" ); //$NON-NLS-1$
		setTitle( Messages.getString("DateLevelDialog.Title") ); //$NON-NLS-1$
		getShell( ).setText( Messages.getString("DateLevelDialog.Shell.Title") ); //$NON-NLS-1$
		setMessage( Messages.getString("DateLevelDialog.Message") ); //$NON-NLS-1$

		Composite area = (Composite) super.createDialogArea( parent );

		Composite contents = new Composite( area, SWT.NONE );
		GridLayout layout = new GridLayout( );
		layout.marginWidth = 20;
		contents.setLayout( layout );
		GridData data = new GridData( GridData.FILL_BOTH );
		data.widthHint = convertWidthInCharsToPixels( 80 );
		data.heightHint = 200;
		contents.setLayoutData( data );

		createContentArea( contents );

		WidgetUtil.createGridPlaceholder( contents, 1, true );

		initLevelDialog( );

		parent.layout( );

		return contents;
	}

	private void initLevelDialog( )
	{
		nameText.setText( input.getName( ) );
		typeCombo.setItems( getAvailableDateTypeDisplayNames( ) );
		typeCombo.setText( getDateTypeDisplayName( input.getDateTimeLevelType( ) ) );
		
		PropertyHandle property = input.getPropertyHandle( GroupElement.INTERVAL_RANGE_PROP );
		String range = property == null ? null : property.getStringValue( );
		intervalRange.setText( range == null ? "" : range ); //$NON-NLS-1$
		int width = intervalRange.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
		( (GridData) intervalRange.getLayoutData( ) ).widthHint = width < 60 ? 60
				: width;
		String interval = input.getInterval( );
		if ( interval == null
				|| interval.equals( DesignChoiceConstants.INTERVAL_TYPE_NONE ) )
		{
			updateRadioButtonStatus( noneIntervalButton );
		}
		else if ( interval.equals( DesignChoiceConstants.INTERVAL_TYPE_INTERVAL ) )
			updateRadioButtonStatus( intervalButton );
		
		
		if ( !noneIntervalButton.getSelection( ) )
		{
			intervalRange.setEnabled( true );
			intervalBaseButton.setEnabled( true );
			intervalBaseButton.setSelection( input.getIntervalBase( ) != null );
			intervalBaseText.setEnabled( intervalBaseButton.getSelection( ) );
			if ( input.getIntervalBase( ) != null )
			{
				intervalBaseText.setText( input.getIntervalBase( ) );
			}
		}


	}

	private void createContentArea( Composite parent )
	{
		Composite content = new Composite( parent, SWT.NONE );
		content.setLayoutData( new GridData( GridData.FILL_BOTH ) );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 2;
		content.setLayout( layout );
		new Label( content, SWT.NONE ).setText( Messages.getString("DateLevelDialog.Name") ); //$NON-NLS-1$
		nameText = new Text( content, SWT.BORDER );
		nameText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		nameText.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				checkOkButtonStatus( );
			}

		} );

		new Label( content, SWT.NONE ).setText( Messages.getString("DateLevelDialog.Type") ); //$NON-NLS-1$
		typeCombo = new Combo( content, SWT.BORDER | SWT.READ_ONLY );
		typeCombo.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		typeCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				checkOkButtonStatus( );
			}

		} );

		Group groupGroup = new Group( content, SWT.NONE );
		layout = new GridLayout( );
		layout.numColumns = 3;
		groupGroup.setLayout( layout );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		groupGroup.setLayoutData( gd );

		new Label( groupGroup, SWT.NONE ).setText( Messages.getString( "LevelPropertyDialog.GroupBy" ) ); //$NON-NLS-1$

		noneIntervalButton = new Button( groupGroup, SWT.RADIO );
		noneIntervalButton.setText( Messages.getString( "LevelPropertyDialog.Button.None" ) ); //$NON-NLS-1$
		noneIntervalButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				updateRadioButtonStatus( noneIntervalButton );
			}
		} );
		intervalButton = new Button( groupGroup, SWT.RADIO );
		intervalButton.setText( Messages.getString( "LevelPropertyDialog.Button.Interval" ) ); //$NON-NLS-1$
		intervalButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				updateRadioButtonStatus( intervalButton );
			}
		} );
		
		new Label( groupGroup, SWT.NONE ).setText( Messages.getString( "LevelPropertyDialog.Label.Range" ) ); //$NON-NLS-1$

		intervalRange = new Text( groupGroup, SWT.SINGLE | SWT.BORDER );
		intervalRange.setLayoutData( new GridData( ) );
		intervalRange.addVerifyListener( new VerifyListener( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
			 */
			public void verifyText( VerifyEvent event )
			{
				if ( event.text.length( ) <= 0 )
				{
					return;
				}

				int beginIndex = Math.min( event.start, event.end );
				int endIndex = Math.max( event.start, event.end );
				String inputtedText = intervalRange.getText( );
				String newString = inputtedText.substring( 0, beginIndex );

				newString += event.text;
				newString += inputtedText.substring( endIndex );

				event.doit = false;

				try
				{
					double value = Double.parseDouble( newString );

					if ( value >= 0 )
					{
						event.doit = true;
					}
				}
				catch ( NumberFormatException e )
				{
					return;
				}
			}
		} );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		intervalRange.setLayoutData( gd );

		intervalBaseButton = new Button( groupGroup, SWT.CHECK );
		intervalBaseButton.setText( Messages.getString("DateLevelDialog.Interval.Base") );  //$NON-NLS-1$
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		intervalBaseButton.setLayoutData( gd );
		intervalBaseButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				intervalBaseText.setEnabled( intervalBaseButton.getSelection( ) );
			}
		} );

		intervalBaseText = new Text( groupGroup, SWT.SINGLE | SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		intervalBaseText.setLayoutData( gd );
	}

	protected void updateRadioButtonStatus( Button button )
	{
		if ( button == noneIntervalButton )
		{
			noneIntervalButton.setSelection( true );
			intervalButton.setSelection( false );
		}
		else if ( button == intervalButton )
		{
			noneIntervalButton.setSelection( false );
			intervalButton.setSelection( true );
		}
		intervalRange.setEnabled( !noneIntervalButton.getSelection( ) );
		intervalBaseButton.setEnabled( !noneIntervalButton.getSelection( ) );
		intervalBaseText.setEnabled( intervalBaseButton.getEnabled( )
				&& intervalBaseButton.getSelection( ) );
	}

	protected void checkOkButtonStatus( )
	{

		if ( nameText.getText( ) == null
				|| nameText.getText( ).trim( ).equals( "" ) //$NON-NLS-1$
				|| typeCombo.getSelectionIndex( ) == -1 )
		{
			if ( getButton( IDialogConstants.OK_ID ) != null )
				getButton( IDialogConstants.OK_ID ).setEnabled( false );
		}
		else
		{
			if ( getButton( IDialogConstants.OK_ID ) != null )
				getButton( IDialogConstants.OK_ID ).setEnabled( true );
		}
	}

	protected void okPressed( )
	{
		try
		{
			if ( nameText.getText( ) != null
					&& !nameText.getText( ).trim( ).equals( "" ) ) //$NON-NLS-1$
			{
				input.setName( nameText.getText( ) );
			}

			if ( typeCombo.getText( ) != null )
			{
				input.setDateTimeLevelType( getAvailableDateTypeNames( ).get( typeCombo.getSelectionIndex( ) )
						.toString( ) );
			}

			if ( noneIntervalButton.getSelection( ) )
				input.setInterval( DesignChoiceConstants.INTERVAL_TYPE_NONE );
			else if ( intervalButton.getSelection( ) )
				input.setInterval( DesignChoiceConstants.INTERVAL_TYPE_INTERVAL );

			if ( !noneIntervalButton.getSelection( ) )
			{
				input.setIntervalRange( intervalRange.getText( ) );
			}
			else
			{
				input.setProperty( GroupHandle.INTERVAL_RANGE_PROP, null );
			}

			if ( intervalBaseText.getEnabled( ) )
			{
				input.setIntervalBase( UIUtil.convertToModelString( intervalBaseText.getText( ),
						false ) );
			}
			else
			{
				input.setIntervalBase( null );
			}
		}
		catch ( Exception e )
		{
			ExceptionHandler.handle( e );
		}
		super.okPressed( );
	}
}

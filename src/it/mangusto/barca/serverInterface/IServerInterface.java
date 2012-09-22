package it.mangusto.barca.serverInterface;

import it.mangusto.barca.serverInterface.ServiceCommunicator.DiscoverDeviceCallBack;

import java.util.Set;

import android.bluetooth.BluetoothDevice;

public interface IServerInterface {
	/**
	 * Controlla che il Bluetooth sia abilitato, se non lo è lo abilita, e si connette al servizio.
	 * Se non è possibile, chiama Main.unusableBlueTooth(BLUETOOTH_ERROR_REASON)
	 */
	public void attemptServiceConnection();
	public void requestServiceDisconnection();
	public int getLastValue();
	/**
	 * Ritorna i devices già trovati e inizia a cercarne di nuovi. Servirsi della DiscoveryDeviceCallBack
	 * per gestire i nuovi device trovati dopo la chiamata a questa funzione(come per esempio aggiungerli a una ArrayAdapter per visualizzarli).
	 *
	 * @param pNewDeviceDiscoveredCallBack Ogni volta che viene rilevato un nuovo device, viene chiamato il metodo di questa Callback
	 * @return Un Set contenente i devices già trovati. Può essere vuoto.
	 */
	public Set<BluetoothDevice> requestDeviceList(DiscoverDeviceCallBack pNewDeviceDiscoveredCallBack);
	
	/**
	 * Connette il servizio a pDevice. Usare requestDeviceList(DiscoverDeviceCallBack) per una lista dei device disponibili.
	 * @param pDevice Il device a cui collegarsi.
	 */
	public void connectToDevice(BluetoothDevice pDevice);
}
